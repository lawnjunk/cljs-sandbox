(ns list-demo.core
  (:require
    ["uuid" :as uuid]
    [oops.core :refer [oget oset!]]
    [ajax.core :refer [GET POST]]
    [reagent.dom]
    [reagent.core :as reagent]
    [re-frame.core :as reframe]
    [clojure.string :as s]))

;; TODO make a simple set of store manuplation funcs
;; set, get, mod, tmp

(defn wait [ms f]
  (js/setTimeout f ms))

(defn reg-sub-simple 
  [thing]
  (reframe/reg-sub 
    thing 
    (fn [db]
      (get db thing)))) ;; dispatch

(defn reg-event-db-simple
  [event thing]
  (reframe/reg-event-db
    event
    (fn [db [_ data]]
      (assoc db thing data))))

(defn store-set-counter
  [counter]
  (reframe/dispatch [:set-counter counter]))
 
(defn counter-inc 
  []
  (let [value @(reframe/subscribe [:counter])]
    (store-set-counter (inc value))))

(reg-event-db-simple :set-spinner-content :spinner-content)
(reg-sub-simple :spinner-content)

(reg-event-db-simple :set-counter :counter)
(reg-sub-simple :counter)

;; view
(defn el-counter []
  (let [counter @(reframe/subscribe [:counter])
        do-dec #(store-set-counter (- counter 1))]
    [:div
     [:button {:on-click #(counter-inc)} "increment"] 
     [:button {:on-click #(do-dec)} "decriment"]
     [:p "the counter: " counter]
     ]))

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

(defn app []
  [:div
   [:h1 "app"]
   [el-counter]
   [el-spinner-test]
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
