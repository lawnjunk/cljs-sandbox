(ns supervisor.data.changelog
  (:require
    [re-frame.core :as reframe]
    [supervisor.side.ldb :as ldb]
    [supervisor.util :as util]))

(reframe/reg-event-fx
  :handle-api-v1-pompom-changlog
  [(ldb/inject)]
  (fn [cofx [_ request-ctx]]
    (let [ldb (:ldb cofx)
          is-success (:is-success request-ctx)
          changelog (get-in request-ctx [:res-data :payload :changelog])]
      (when is-success
        {:ldb (assoc ldb :changelog changelog)}))))

(reframe/reg-sub
  :changelog-fetch
  (ldb/signal)
  (fn [ldb]
    (get ldb :changelog)))

(defn fetch
  []
  (reframe/subscribe [:changelog-fetch]))

