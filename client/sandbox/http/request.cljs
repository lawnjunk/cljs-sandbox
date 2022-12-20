(ns sandbox.http.request
  (:require
    [ajax.core :refer [POST]]
    [sandbox.util :as util]
    [sandbox.data.request-ctx :as request-ctx]))

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

(defn- handle-success
  [request-id response]
  (let [res-data response]
    (request-ctx/update-success request-id res-data)))


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

; TODO make it so that it will allow req-data to be nil
(defn request
  ":url api endpoint
   (:req-data) to send as json
   (:request-id) request-id
   (:timeout-in-ms) defalt 0 (none)"
  [options]
  (let [request-id (get options :request-id (util/id-gen))
        url (str "http://localhost:7766" (get options :url))
        params (get options :req-data)
        timeout (util/clamp-min (get options :timeout-in-ms 0) 0)
        format-options (if-not  params {} {:format :json :params params}) 
        ajax (POST 
               url 
               (merge  
                 format-options
                 {:response-format :json
                  :keywords? true
                  :timeout timeout
                  :handler (partial handle-success request-id)
                  :error-handler (partial handle-error request-id)}))  
        ctx (request-ctx/create url request-id params ajax)] 
    (request-ctx/put ctx)))
