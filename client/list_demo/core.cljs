(ns list-demo.core
  (:require
    ["uuid" :as uuid]
    [oops.core :refer [oget oset!]]
    [ajax.core :refer [POST]]
    [reagent.dom]
    [reagent.core :as reagent]
    [re-frame.core :as reframe]
    [clojure.string :as s]))

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
  [thing]
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
    {:get thing-get
     :set thing-set
     :del thing-del
     :mod thing-mod
     :tmp thing-tmp})))

(def lala-store (simple-store-create :lala))
((:set lala-store) "cool")

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
    [:div
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
   :handler
   "
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
                          (handler {:success true :data response}))
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
                                               :data data})))))})))

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
                 (if success
                   (reframe/dispatch [:set-spinner-content (get data "content" "gooo")])
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

(defn app []
  [:div
   [:h1 "app"]
   [el-lala]
   [el-counter]
   [el-spinner-test]
   [el-lucky-number]
   ])

(defn render []
  (let [container (js/document.getElementById "app-container")]
    (reagent.dom/render [app] container)))

(defn ^:dev/after-load render-app []
  (js/console.log "after-load start") 
  (render))

(defn init []
  (js/console.log "init ")
  (store-set-counter 666)
  ((:tmp tmp-lucky-number-store) 2000 (js/Math.floor (* 100 (js/Math.random))))
  (render-app))

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
