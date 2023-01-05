(ns supervisor.data.pomelo-metadata
  (:require
    [re-frame.core :as reframe]))

(reframe/reg-event-fx
  :handle-api-v1-supervisor-pomelo-metadata-fetch
  (fn [cofx [_ request-ctx]]
    (let [db (:db cofx)
          is-success (:is-success request-ctx)
          payload (get-in request-ctx [:res-data :payload])]
      (when is-success
        {:db (assoc db :pomelo-metadata payload)}))))

(reframe/reg-sub
  :pomelo-metadata-fetch
  (fn [db]
    (get db :pomelo-metadata)))

(defn fetch
  []
  (reframe/subscribe [:pomelo-metadata-fetch]))
