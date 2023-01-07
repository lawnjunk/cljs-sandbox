(ns supervisor.data.search-bar
 (:require
   [re-frame.core :as reframe]
   [supervisor.side.qdb :as qdb]
   [supervisor.util :as util]))

(defn create-search-key
  [search-term]
  (->> search-term
      (name)
      (str "search-")
      (keyword)))

(reframe/reg-event-fx
  :search-bar-update-term
  [(qdb/inject)]
  (fn [cofx [_ search-term value]]
    (let [qdb (:qdb cofx)
          search-key (create-search-key search-term)]
      (util/pp "search-key" search-key :value value qdb)
      {:qdb (assoc qdb search-key value)})))

(reframe/reg-sub
  :search-bar-fetch-term
  (qdb/signal)
  (fn [qdb [_ search-term]]
    (let [search-key (create-search-key search-term)
          value (get qdb search-key "")]
      (if (= 0 value) "" value))))


(defn update-term
  [search-term value]
  (reframe/dispatch [:search-bar-update-term search-term value]))

(defn fetch-term
  [search-term]
  (reframe/subscribe [:search-bar-fetch-term search-term]))
