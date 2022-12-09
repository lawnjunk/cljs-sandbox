(ns list-demo.core
  (:require
    [reagent.dom]
    [re-frame.core :as reframe]
    [clojure.string :as str]))

;; dispatch
(defn store-set-value
  [value ]
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
   [:h1 "this is a test"]
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
