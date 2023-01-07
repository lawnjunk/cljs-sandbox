; qdb works like reframe's app-db but it will also update the query-string

(ns supervisor.side.qdb
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as reframe]
    [supervisor.location :as location]))

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

(reframe/reg-event-fx
  ::qdb-overwrite
  [(inject)]
  (fn [_ [_ query-data]]
      {:qdb query-data}))

(defn overwrite!
  [query]
  (reframe/dispatch [::qdb-overwrite query]))

(reframe/reg-sub
  ::qdb-fetch
  (signal)
  (fn [db] db))

(reframe/reg-sub
  ::qdb-fetch-key
  (signal)
  (fn [db [_ k]]
    (get db k)))

(defn fetch
  ([]
   (reframe/subscribe [::qdb-fetch]))
  ([k]
   (reframe/subscribe [::qdb-fetch-key k])))

(defn hydrate!
  []
  (println "qdb-hydrate!")
  (-> (location/fetch-map-from-query)
      (overwrite!)))
