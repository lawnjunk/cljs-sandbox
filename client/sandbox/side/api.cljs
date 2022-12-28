(ns sandbox.side.api
  (:require
    [re-frame.core :as reframe]
    [sandbox.util :as util]
    [sandbox.side.ldb :as ldb]
    [sandbox.side.ajax :as ajax]))

(reframe/reg-event-fx
  :api-request
  [(ldb/inject)]
  (fn [cofx [_ request-options]]
    (println "api-request fx")
    (let [ldb (:ldb cofx)
          auth-required (get request-options :auth-required)
          auth-token (get ldb :auth-token "none")
          auth-header {:x-supervisor-token auth-token}
          header (-> (get request-options :req-header)
                      (merge (when auth-required auth-header)))]
      {:api (util/xxdp (merge request-options {:req-header header}))})))

(reframe/reg-fx
  :api
  (fn [request-options]
    (ajax/raw-request request-options)))

(defn request
  "create an http request and store it as request-ctx in app-db
      - see sandbox.data.request-ctx

   request-id can be used to fetch the request-ctx later
     (:request-id) request-id

   to trigger events on finish use
     (:fx) [[:dispatch [:debug :some-tag]]] (request-ctx will NOT be conjed)
     (:dispatch) [:debug :some-tag]         (request-ctx will be conjed)

   + (:auth-required boolean) to auto add auth header

   request config options
     (:url) route will not work if url is set
     :route will make request to environ/API_URL/{route}
     (:method) :post :get ...etc      (default to :post)
     (:req-format) :raw :json :none   (default to :json if :req-data is set)
                                      (default to :none if :req-data not set)
     (:res-format) :raw :json :none   (defalut to :json)
     (:req-data) data to send in body (can be FormData if :res-format is :raw)
     (:req-query) request query-string map
     (:req-header) request header map
     (:timeout-in-ms) (default 0 none)"
  [request-options]
  (reframe/dispatch [:api-request request-options]))
