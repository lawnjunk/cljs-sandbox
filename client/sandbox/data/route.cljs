(ns sandbox.data.route
  (:require
    [re-frame.core :as reframe]))

(def route-default
  {:page :landing :id nil :query {}})

(reframe/reg-event-db
  :route-set
  (fn  [db [_ page query id]]
    (assoc db :route 
           {:page page 
            :id id 
            :query (if query query nil)})))

(defn goto [page query id]
  (reframe/dispatch [:route-set page query id]))

(reframe/reg-sub
  :route-fetch
  (fn [db]
    (get db :route route-default)))

(defn fetch []
  (reframe/subscribe [:route-fetch]))
