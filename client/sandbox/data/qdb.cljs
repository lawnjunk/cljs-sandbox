(ns sandbox.data.qdb
  (:require
    [re-frame.core :as reframe]
    [sandbox.location :as location]
    [sandbox.side.qdb :as qdb]
    [sandbox.util :as util]))

(reframe/reg-event-fx
  ::qdb-overwrite
  [(qdb/inject)]
  (fn [_ [_ query-data]]
      {:qdb query-data}))

(defn overwrite!
  [query]
  (reframe/dispatch [::qdb-overwrite query]))

(reframe/reg-sub
  ::qdb-fetch
  (qdb/signal)
  (fn [db] db))

(reframe/reg-sub
  ::qdb-fetch-key
  (qdb/signal)
  (fn [db [_ k]]
    (get db k)))

(defn fetch
  ([]
   (reframe/subscribe [::qdb-fetch]))
  ([k]
   (reframe/subscribe [::qdb-fetch-key k])))
