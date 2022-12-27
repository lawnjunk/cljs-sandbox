(ns sandbox.side.api
  (:require
    [re-frame.core :as reframe]
    [sandbox.side.ldb :as ldb]
    [sandbox.http.request :refer [request]]))

(reframe/reg-event-fx
  :api
  [(ldb/inject)]
  (fn [cofx [_ request-options]]
    (let [ldb (:ldb cofx)
          auth-required (get request-options :auth-required?)
          auth-token (get ldb :auth-token "")
          auth-header {:x-supervisor-token auth-token}
          header (-> (get request-options :header)
                      (merge (when auth-required auth-header)))]
      {:api (merge request-options {:header header})})))

(reframe/reg-fx
  :api
  (fn [request-options]
    (request request-options)))
