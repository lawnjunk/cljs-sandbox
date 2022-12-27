(ns sandbox.http.spinner
  (:require 
    [sandbox.http.request :refer [request]]))

(defn http-spinner [request-id]
  (request
    {:url "/api/spinner"
     :request-id request-id
     }))
