; qdb works like reframe's app-db but it will also update the store

(ns sandbox.side.qdb
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as reframe]
    [sandbox.location :as location]))

(def app-qdb (reagent/atom {}))

(reframe/reg-cofx
  :qdb
  (fn [cofx]
    (assoc cofx :qdb (location/fetch-map-from-query))))

(reframe/reg-fx
  :qdb
  (fn [qdb]
    (reset! app-qdb qdb)
    (location/replace-query-from-map! qdb)))

(defn signal[]
  (fn [_ _] 
    app-qdb))

(defn inject [] 
  (reframe/inject-cofx :qdb))
