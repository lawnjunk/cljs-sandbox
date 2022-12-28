;lldb works like reframe's app-db but it will also set local storage

(ns supervisor.side.ldb
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as reframe]
    [clojure.edn :as edn]
    [oops.core :as oops]))

(def app-ldb (reagent/atom {}))

(reframe/reg-cofx
  :ldb
  (fn [cofx]
    (assoc cofx :ldb @app-ldb)))

(reframe/reg-fx
  :ldb
  (fn [ldb]
    (reset! app-ldb ldb)
    (.setItem js/window.localStorage "store" (pr-str ldb))))

(defn signal[]
  (fn [_ _]
    app-ldb))

(defn inject []
  (reframe/inject-cofx :ldb))

(reframe/reg-event-fx
  ::ldb-overwrite
  [(inject)]
  (fn [_ [_ query-data]]
      {:ldb query-data}))

(defn overwrite!
  [query]
  (reframe/dispatch [::ldb-overwrite query]))

(reframe/reg-sub
  ::ldb-fetch
  (signal)
  (fn [db] db))

(reframe/reg-sub
  ::ldb-fetch-key
  (signal)
  (fn [db [_ k]]
    (get db k)))

(defn fetch
  ([]
   (reframe/subscribe [::ldb-fetch]))
  ([k]
   (reframe/subscribe [::ldb-fetch-key k])))

(defn erase! []
  (println "ldb-erase!")
  (reset! app-ldb {})
  (js-delete js/window.localStorage "store"))


(defn hydrate!
  "hydrate lds from local-storage"
  []
  (println "ldb-hydrate!")
  (-> (or (.getItem js/localStorage "store") "") 
      (edn/read-string)
      (overwrite!)))

