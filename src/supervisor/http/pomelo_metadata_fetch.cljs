(ns supervisor.http.pomelo-metadata-fetch
  (:require
    [re-frame.core :as reframe]
    [supervisor.side.api :as api]
    ))

(defn api-fx
  "v1/supervisor/pomelo-metadata-fetch"
  [request-id]
  {:request-id request-id
   :route "/v1/supervisor/pomelo-metadata-fetch"
   :auth-required true
   :fx [[:dispatch [:handle-api-v1-supervisor-pomelo-metadata-fetch ]]]})

(defn request
  [request-id]
  (api/request (api-fx request-id)))
