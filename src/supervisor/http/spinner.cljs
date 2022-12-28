(ns supervisor.http.spinner
  (:require
    [supervisor.side.api :refer [request]]))

(defn http-spinner [request-id]
  (request
    {:route "/api/spinner"
     :request-id request-id
     :dispatch [:debug :http-spinner]
     :auth-required true
     }))
