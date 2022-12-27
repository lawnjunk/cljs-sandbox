(ns sandbox.http.spinner
  (:require
    [sandbox.http.request :refer [request]]))

(defn http-spinner [request-id]
  (request
    {:url "http://localhost:7766/api/spinner"
     :request-id request-id
     :dispatch [:debug :http-spinner]
     }))
