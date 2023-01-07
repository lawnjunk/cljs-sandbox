(ns supervisor.http.pompom-changelog
  (:require
    [supervisor.side.api :as api]))

(defn api-fx
  "v1/pompom/changelog"
  [request-id]
  {:request-id request-id
   :route "/v1/pompom/changelog"
   :method :get
   :fx [[:dispatch [:handle-api-v1-pompom-changlog]]]
   })

(defn request
  [request-id]
  (api/request (api-fx request-id)))
