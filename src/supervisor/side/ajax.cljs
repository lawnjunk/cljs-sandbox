; a wrapper around cljs-ajax that tracks the requests
; using supervisor.data.request-ctx
; you should probbly not use this file diretly
; if you want to make an ajax request use supervisor.side.api/request

(ns supervisor.side.ajax
  (:require
    [ajax.core :as ajax]
    [ajax.protocols :as protocols]
    [ajax.json :refer [read-json-native]]
    [ajax.interceptors :as interceptors]
    [supervisor.util :as util]
    [supervisor.data.request-ctx :as request-ctx]
    [supervisor.environ :as environ]))

; (defn handle-progress [e]
;   (util/xxl e)
;   (println (str "Progress (" (.-loaded e) "/" (.-total e) ")")))

(def custom-json-response-format
   (interceptors/map->ResponseFormat
     {:description "custom json response format"
      :content-type ["application/json"]
      :read (fn [xhrio] 
              (let [header (protocols/-get-all-headers xhrio)
                    text (protocols/-body xhrio)
                    data (when (not (empty? text))
                           (read-json-native false true text))]
              {:res-data data
               :res-text text
               :was-aborted (protocols/-was-aborted xhrio)
               :res-status (protocols/-status xhrio)
               :res-header (util/keywordify (or header {})) }))}))

(def custom-raw-response-format
   (interceptors/map->ResponseFormat
     {:description "custom raw response format"
      :content-type ["*/*"]
      :read (fn [xhrio]
              {:res-data (protocols/-body xhrio)
               :res-text (protocols/-body xhrio)
               :was-aborted (protocols/-was-aborted xhrio)
               :res-status (protocols/-status xhrio)
               :res-header (util/keywordify (or (protocols/-get-all-headers xhrio) {})) })}))

(defn- error-response-get-error
  "parse the error-response to create a usefull error map"
  [response]
  (let [why (get response :failure)
        why (if (= why :error) :server why)
        status (get response :status)
        message (get response :status-text)
        hint (case why
                  :server (str "server error: " status)
                  :aborted "client aborted request"
                  :failed "no server or server closed connection"
                  :timeout "request timeout"
                  :parse (get response :parse-error "unknown error")
                  "unknown error!!!")]
    {:type :request-error
     :message message
     :status (get response :status)
     :why why
     :hint hint}))

(defn- handle-error
  [request-id error-response]
  (let [error (error-response-get-error error-response)
        response (:response error-response)]
    (request-ctx/update-error request-id response error)))

(defn- handle-success
  [request-id response]
    (request-ctx/update-success request-id response))

(defn- create-request-handler
  [request-id]
  (fn [[ok response]]
    ; there is a open bug for 204 where response is always nil
    ; https://github.com/JulianBirch/cljs-ajax/issues/251
    ; this means the custom parsers will never run
    ; so things like headers and status will not be accesable from 204
    (let [response-204 {:res-status 204 :res-body nil :res-text nil
                        :was-aborted nil :res-data nil :res-header {}}
          ; status 0 will  occur when express calls res.end()
          ; status 0 will also occur if server is not running
          response-abort {:status 0
                          :failure :failed
                          :status-text "no server or server closed connection"
                          :response (merge response-204
                                           {:res-status 0
                                            :was-aborted false})}
          response (or response response-204)
          status (:status response)
          response (if (= 0 status) response-abort response)]
      (if ok
        (handle-success request-id response)
        (handle-error request-id response)))))

(defn- get-response-format
  ":none and :raw both need raw"
  [format-type]
  (case format-type
    :json custom-json-response-format 
    custom-raw-response-format))

(defn- get-request-format
  ":none and :raw both need raw"
  [format-type]
  (case format-type
    :json (ajax/json-request-format)
    (ajax/text-request-format)))

(defn raw-request
  "read the supervisor.side.api/request docs"
  [options]
  (let [request-id (get options :request-id (util/id-gen))
        fx (get :fx options)
        dispatch (get :dispatch options)
        url (:url options)
        url (if url url (str environ/API_URI (:route options)))
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
                })]
        (request-ctx/put
          {:request-id request-id
           :pending true
           :is-success nil
           :was-aborted nil
           :error nil
           :fx fx
           :dispatch dispatch
           :ajax ajax
           :res-data nil
           :res-text nil
           :res-status nil
           :res-header nil
           :req-url url
           :req-method method
           :req-format req-format
           :res-format res-format
           :req-data req-data
           :req-header req-header
           :req-query req-query
           :req-timeout-in-ms timeout-in-ms})))
