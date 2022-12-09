(ns list-demo.core
  (:require
    [ajax.core :refer [GET POST]]
    [reagent.dom]
    [re-frame.core :as reframe]
    [clojure.string :as str]))

;; request
(defn req-item-list-fetch []
  (println "making request")
  (POST "http://localhost:7766/api/item-list-fetch"
     {:handler (fn [response cool]
               (println "cool beans" response cool))
      :response-format :json
      :error-handler (fn [response]
                     (println "fuckk beans" response))}))

(defn req-item-create [name]
  (POST "http://localhost:7766/api/item-create"
     {:format :json
      :params {:name name}
      :handler (fn [response cool]
               (println "item create" response cool))
      :response-format :json
      :error-handler (fn [response]
                     (println "fuckk beans" response))}))

;(defonce result (req-item-create "get milk"))
(defonce result (req-item-list-fetch))

;; dispatch
(defn store-set-value
  [value]
  (reframe/dispatch [:set-value value]))

(defonce goo (store-set-value 666))

(reframe/reg-event-db 
  :set-value
  (fn [db [_ value]]
       (assoc db :value value)))

;; query
(reframe/reg-sub
  :value
  (fn [db _]
    (:value db)))

;; view
(defn value-view []
  (let [value @(reframe/subscribe [:value])
        do-inc #(store-set-value (inc value))
        do-dec #(store-set-value (- value 1))]
    [:div
     [:button {:on-click #(do-inc)} "increment"] 
     [:button {:on-click #(do-dec)} "decriment"]
     [:p "the value: " value]
     ]))

(defn app []
  [:div
   [:h1 "this is a a test"]
   [value-view]
   ])

(defn render []
  (let [container (js/document.getElementById "app-container")]
    (reagent.dom/render [app] container)))

(defn ^:dev/after-load start []
  (js/console.log "after-load start") 
  (render))

(defn init []
  (js/console.log "init ")
  (start))

(defn ^:dev/before-load stop []
  (js/console.log  "before-load stop"))
