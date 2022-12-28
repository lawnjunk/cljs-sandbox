; a wrapper around cljs-ajax that tracks the requests 
; using sandbox.data.request-ctx

(ns sandbox.http.request
  (:require
    [ajax.core :as ajax]
    [sandbox.util :as util]
    [sandbox.data.request-ctx :as request-ctx]))

; (defn handle-progress [e]
;   (util/xxl e)
;   (println (str "Progress (" (.-loaded e) "/" (.-total e) ")")))

(defn- error-response-get-data
  [response]
  (let [data (get response :response)
        text (get response :original-text)]
    (or data text)))

; {:type request-error
;  :why #{:server :parse :aborted :timeout}
;  :status 999
;  :content "something whent wrong"}
(defn- error-response-get-error
  [response]
  (let [why (get response :failure)
        why (if (= why :error) :server why)
        status (get response :status)
        content (case why
                  :server (str "server error: " status)                  
                  :aborted "request aborted"
                  :timeout "request timeout"
                  :parse (get response :parse-error "unknown error")
                  "unknown error!!!")]
    {:type :request-error
     :why why
     :status (get response :status)
     :content content}))

(defn- handle-error
  [request-id response]
  (let [status (get response :status 999)
        res-data (error-response-get-data response)
        error (error-response-get-error response)
        why (:why error)
        ; request will allways give a :parse error on 201
        is-success (and (= status 201) (= why :parse))]
    (if is-success
      (request-ctx/update-success request-id res-data error)
      (request-ctx/update-error request-id res-data error))))

(defn- handle-success
  [request-id response]
  (let [res-data response]
    (request-ctx/update-success request-id res-data)))

(defn- create-request-handler
  [request-id]
  (fn [[ok response]]
    (if ok
      (handle-success request-id response)
      (handle-error request-id response))))

(defn- get-response-format
  [format-type]
  (case format-type
    :raw (ajax/raw-response-format)
    :none nil
    (ajax/json-response-format {:keywords? true})))

(defn- get-request-format
  [format-type]
  (case format-type
    :none nil
    :raw (ajax/text-request-format)
    (ajax/json-request-format)))

; TODO suport :form-data https://github.com/JulianBirch/cljs-ajax#getpost-examples
(defn request
  "create an http request and store it as request-ctx in app-db
      - see sandbox.data.request-ctx

   to trigger events on finish use 
     (:fx) [[:dispatch [:debug :some-tag]]] (request-ctx will NOT be conjed)
     (:dispatch) [:debug :some-tag]         (request-ctx will be conjed)

   request config options
     :url **required**
     (:request-id) request-id
     (:method) :post :get ...etc      (default to :post)
     (:req-format) :raw :json :none   (default to :json if :req-data is set)
                                      (default to :none if :req-data not set)
     (:res-format) :raw :json :none   (defalut to :json)
     (:req-data) data to send in body (can be FormData if :res-format is :raw)
     (:req-query) request query-string map
     (:req-header) request header map
     (:timeout-in-ms) (default 0 none)"
  [options]
  (let [request-id (get options :request-id (util/id-gen))
        fx (get :fx options)
        dispatch (get :dispatch options)
        url (str "" (get options :url))
        method (get options :method :post)
        req-data (:req-data options)
        req-format (get options :req-format (when req-data :json))
        res-format (get options :res-format :json)
        req-format-handler (get-request-format req-format)
        res-format-handler (get-response-format res-format)
        req-header (merge (get options :req-header)
                          {:request-tag request-id})
        req-query  (get options :req-query)
        timeout-in-ms (util/clamp-min (get options :timeout-in-ms 0) 0)
        ajax (ajax/ajax-request
               {:uri url
                :method method
                :header req-header
                :url-params req-query
                :timeout timeout-in-ms
                :format req-format-handler
                :response-format res-format-handler
                :body (when (= :raw req-format) req-data)
                :params (when (= :json req-format) req-data)
                :handler (create-request-handler request-id)
                })
        request-ctx {:request-id request-id
             :pending true
             :is-success nil
             :error nil
             :response nil
             :fx fx
             :dispatch dispatch
             :ajax ajax
             :url url
             :method method
             :req-format req-format
             :res-format res-format
             :req-data req-data
             :req-header req-header
             :req-query req-query
             :timeout-in-ms timeout-in-ms
             }]
    (request-ctx/put request-ctx)))
