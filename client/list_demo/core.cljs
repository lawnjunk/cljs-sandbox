(ns list-demo.core
  (:require
    ["uuid" :as uuid]
    [clojure.walk :as walk]
    [oops.core :refer [oget oset!]]
    [ajax.core :refer [POST]]
    [reagent.dom]
    [reagent.core :as reagent]
    [re-frame.core :as reframe]
    [secretary.core :as secretary]
    [spade.core :refer [defclass]]
    [clojure.string :as s])
  (:require-macros
    [secretary.core :refer [defroute]]))

(def keywordify walk/keywordize-keys)

(defn get-uri-hash []
  (let [hash js/window.location.hash]
    (if hash hash "")))

(def log js/console.log)


(defclass css-basic []
  {:background "#fff"
   :color "black"
   :font-weight "normal"
   :padding "10px"
   :margin-top "10px"
   })

(defclass css-blue-box []
  {:background "#a0a0ff"
   :padding "15px"})

(defclass css-counter-color [num]
  (if (even? num) {:background "red"} {:background "orange"}))

(defclass css-f-button []
   {:background "blue"
    :color "white"
    :padding "5px"
    :margin-top "5px"
    :margin-right "5px"
    :font-weight "bold" }
   [:&:hover
    {:background "green" }]
   [:&:active
    {:background "red" }])

(defn vconcat [a b] (vec (concat a b )))

(defn el-create
  [tag-name style]
  (fn [options & children]
    (vconcat [tag-name (merge {:class (style)} options)] children)))

(def Button (el-create :button css-f-button))
(def BlueBox (el-create :div css-blue-box))

(defn wait [ms f]
  (js/setTimeout f ms))

(defn reg-sub-simple
  [thing]
  (reframe/reg-sub
    thing
    (fn [db]
      (get db thing)))) ;; dispatch

(defn reg-event-db-simple-set
  [set-event thing]
  (reframe/reg-event-db
    set-event
    (fn [db [_ data]]
      (assoc db thing data))))

(defn reg-event-db-simple-del
  [del-event thing]
  (reframe/reg-event-db
    del-event
    (fn [db []]
      (dissoc db thing))))

(defn store-set-counter
  [counter]
  (reframe/dispatch [:set-counter counter]))

(defn simple-store-create
  "thing should be a :keyword that holds
   a value in the root of the global store"
  ([thing]
    (simple-store-create thing nil))
  ([thing default]
    (let [thing-set-keyword (s/replace (str thing) ":" ":set-")
          thing-del-keyword (s/replace (str thing) ":" ":del-")]
      (reg-event-db-simple-set thing-set-keyword thing)
      (reg-event-db-simple-del thing-del-keyword thing)
      (reg-sub-simple thing)
      (let [thing-get (fn []
                     @(reframe/subscribe [thing]))
            thing-set (fn [value]
                     (reframe/dispatch [thing-set-keyword value]))
            thing-del (fn []
                     (reframe/dispatch [thing-del-keyword]))
            thing-tmp (fn [delayInMS value]
                     (thing-set value)
                     (wait delayInMS #(thing-del)))
            thing-mod (fn [handler]
                     (thing-set (handler (thing-get))))]
        (if-not (nil? default) (thing-set default))
      {:get thing-get
       :set thing-set
       :del thing-del
       :mod thing-mod
       :tmp thing-tmp}))))

(defonce lala-store (simple-store-create :lala "cool"))
(defonce route-store (simple-store-create :route {:page "/" :args []}))

(def tmp-lucky-number-store (simple-store-create :tmp-lucky-number))

(defn counter-inc
  []
  (let [value @(reframe/subscribe [:counter])]
    (store-set-counter (inc value))))

(reg-event-db-simple-set :set-spinner-content :spinner-content)
(reg-sub-simple :spinner-content)

(reg-event-db-simple-set :set-counter :counter)
(reg-sub-simple :counter)

;; view
(defn el-counter []
  (let [counter @(reframe/subscribe [:counter])
        do-dec #(store-set-counter (- counter 1))]
    [:div {:class (css-counter-color (if (number? counter) counter 0))}
     [:button {:on-click #(counter-inc)} "increment"] 
     [:button {:on-click #(do-dec)} "decriment"]
     [:p "the counter: " counter]]))

(defn req-ctx-create
  ([url]
    (req-ctx-create url (uuid/v4)))
  ([url tt]
    {:tt tt
     :url url
     :pending true
     :error nil
     :success nil}))

(defn dbg
  ([stuff]
    (println "DBG: " stuff)
    stuff)
  ([title stuff]
    (println "DBG" title ">>" stuff)
    stuff))

(reframe/reg-event-db
  :req-ctx-set
  (fn [db [_ counter]]
       (let [tt (:tt counter)
             req-store (get db :req-store {})]
         (dbg  (assoc db :req-store (assoc req-store tt counter))))))

(reframe/reg-event-db
  :req-ctx-success
  (fn [db [_ tt]]
       (let [req-store (get db :req-store {})
             ctx (-> (get req-store tt)
                     (assoc :pending false)
                     (assoc :success true))] 
         (assoc db :req-store (assoc req-store tt ctx)))))

(reframe/reg-event-db
  :req-ctx-error
  (fn [db [_ tt]]
       (let [req-store (get db :req-store {})
             ctx (-> (get req-store tt)
                     (assoc :pending false)
                     (assoc :success false)) ]
         (assoc db :req-store (assoc req-store tt ctx)))))

(reframe/reg-sub
  :tt
  (fn [db stuff]
    (get (:req-store db) (second stuff))))

(defn request
  ":tt optional tt
   :url api endpoint
   :payload data to send as json
   :handler"
  [data]
  (let [tt (get data :tt (uuid/v4))
        handler (get data :handler (partial println "DEFAULT_HANDLER:" ))
        url (str "http://localhost:7766" (get data :url))
        payload (get data :payload)
        ctx (req-ctx-create url tt)]
    (println "REQUEST" url payload)
    (reframe/dispatch [:req-ctx-set ctx])
    (POST url {
               :format :json
               :response-format :json
               :params (if (nil? payload) {} payload)
               :handler (fn [response]
                          (reframe/dispatch [:req-ctx-success tt])
                          (handler {:success true :data (keywordify response)}))
               :error-handler (fn [response]
                                (let [status (:status response)
                                      success (contains? #{200 201} status)
                                      data (get response :response nil)]
                                  (if success
                                    ((reframe/dispatch [:req-ctx-success tt])
                                     (handler {:success success
                                               :data data}))
                                    ((reframe/dispatch [:req-ctx-error tt response])
                                     (handler {:success success
                                               :data (keywordify data)})))))})))

(defn req-debug [tt status delayInMS payload handler]
  (request
    { :tt tt
      :url "/api/debug"
      :handler handler
      :payload {:status status
                :delayInMS delayInMS
                :payload payload}}))



(def el-spinner-tt (reagent/atom (uuid/v4)))

(defn req-spinner-test [tt]
  (req-debug tt 200 2000 {:content "ping pong"} 
             (fn [result]
               (wait 2000 #(reset! el-spinner-tt (uuid/v4)))
               (let [{success :success data :data} result]
                 (println "new data" data)
                 (if success
                   (reframe/dispatch [:set-spinner-content (get data :content "gooo")])
                   (reframe/dispatch [:set-spinner-content "THERE WAS AN ERROR"]))))))

(defn el-spinner-test []
  (let [content @(reframe/subscribe [:spinner-content])
        content (if (nil? content) "no content" content)
        tt @el-spinner-tt
        req-ctx @(reframe/subscribe [:tt tt])]
    [:div
     [:button {:on-click (partial req-spinner-test tt)} "click me"]
     (if (not (nil? req-ctx))
       (let [pending (:pending req-ctx)
             pending-info (str pending)
             success (:success req-ctx)
             success-info (if success  (str success) "unknown")]
         [:div
           [:p "pending: " pending-info]
           [:p "success: " success-info]
           (if (not pending)
             [:div content])]))]))

(defn el-lala []
  (let [lala-data ((:get lala-store))]
    [:div
     [:button {:on-click #((:mod lala-store) (fn [d] (str d "!!")))} "click for more !"]
     [:p "this is lala data: " lala-data]]))

(defn el-lucky-number []
  (let [value ((:get tmp-lucky-number-store))]
    (if value [:div {:class "hello"}
               [:h1 "you are lucky if you can see this"]
               [:h2 value]])))

(defn page-1 []
  [:div "page 1 is a humble simple page"])

(defn page-2 [hash-id]
  [:div "page 2 has a hash-id" " " hash-id])

(secretary/set-config! :prefix "#")

(defroute route-page-2-empty "/goop" []
  ((:set route-store) {:page :second :args ["none project seleced"]}))

(defroute route-page-2 "/goop/:id" [id]
  ((:set route-store) {:page :second :args [id]}))

(defroute route-page-1 "*" []
  ((:set route-store) {:page :landing :args []}))


(defn app []
  (let [route ((:get route-store))]
    (println "route" route)
    [:div
     (case (:page route)
       :landing [page-1]
       :second (vconcat [page-2] (:args route))
       [page-1])
     [:h1 "app"]
     [el-lala]
     [el-counter]
     [el-spinner-test]
     [el-lucky-number]
     [BlueBox {} "c ooooool hhaha i mean"
      [BlueBox {:class (css-basic)} "isnt" 
       [BlueBox {} "it"]]
       [BlueBox {:class (css-basic)} "real nuts"] 
      ]
     [:a {:href "/#/beans"} "goto bean"]
     [:a {:href "/#/goop "} "goto goop"]
     [:a {:href "/#/goop/zip123 "} "goto goop goop"]
     [Button {:on-click #(println "i was clicked" (js/Math.random))} "HEELO"]
     [Button {} "cool beans"]
     [Button {} "multi" " children"]
     (map #(identity [Button
                      {:key (str "wat" %)
                       :on-click (partial println "haha" %)}
                      (str "clickr # " %)])
          (range 0 10))
   ]))


(defn render []
  (let [container (js/document.getElementById "app-container")]
    (reagent.dom/render [app] container)))

(defn ^:dev/before-load stop []
  (js/console.log  "before-load stop"))

;; request
(defn req-item-list-fetch []
  (request
    { :url "/api/item-list-fetch"}))

(defn req-item-create [name]
  (request
    { :url "/api/item-create"
      :payload { :name name }}))

(defn ^:dev/after-load render-app []
  (js/console.log "after-load start") 
  (reframe/clear-subscription-cache!)
  (render))

(defn init []
  (secretary/dispatch! (get-uri-hash))
  (js/window.addEventListener "hashchange" #(secretary/dispatch! (get-uri-hash)))
  (js/console.log "init ")
  (store-set-counter 666)
  ((:tmp tmp-lucky-number-store) 2000 (js/Math.floor (* 100 (js/Math.random))))
  (render-app))

