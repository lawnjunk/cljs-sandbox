(ns sandbox.data.query
  (:require
    [re-frame.core :as reframe]
    [sandbox.location :as location]
    [sandbox.util :as util]))

(reframe/reg-event-fx
  ::query-write-from-data
  (fn [cofx [_ query-data]]
    (-> query-data
        (location/query-data-from-map)
        (location/query-data-to-string)
        (location/replace-query!))

    (let [db (:db cofx)]
      (println "haha" query-data)
      {:db (assoc db :query query-data)})))

(reframe/reg-event-fx
  ::query-write-from-string
  (fn [cofx [_ query-string]]
     (let [db (:db cofx)
          query-data (location/query-data-from-string query-string)] 
       (-> query-string
          (location/query-data-from-string)
          (location/query-data-to-string)
          (location/replace-query!))
       (println "haha" query-string)
       {:db (assoc db :query (location/decode-uri-encoded-values query-data))})))

(defn write-data!
  [query]
  (reframe/dispatch [::query-write-from-data query]))

(reframe/reg-sub
  ::query-fetch
  (fn [db]
    (get db :query {})))

(reframe/reg-sub
  ::query-fetch-key
  (fn [db [_ k]]
    (get-in db [:query k])))

(defn fetch
  ([]
   (reframe/subscribe [::query-fetch]))
  ([k]
   (reframe/subscribe [::query-fetch-key k])))


;(write-data! {:text "hello @&coolworld" :cool :ziip :sweet [1 :two "tree" 231] :num 12346.54})
