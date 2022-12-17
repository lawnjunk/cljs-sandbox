(ns sandbox.data.request-ctx
  (:require
    [re-frame.core :as reframe]
    [sandbox.util :as util]))

(defn create
  ([url]
    (create url (util/id)))
  ([url request-id]
    {:request-id request-id
     :url url
     :pending true
     :response nil
     :error nil
     :success nil}))

(reframe/reg-event-db
  :request-ctx-put
  (fn [db [_ request-ctx]]
       (let [request-id (:request-id request-ctx)
             request-ctx-stash (get db :request-ctx-stash {})]
         (util/xxdp "request-ctx-put" (->> request-ctx
              (assoc request-ctx-stash request-id)
              (assoc db :request-ctx-stash))))))

(defn put
  "put a new request-ctx into the request-ctx-stash"
  [request-ctx]
  (reframe/dispatch
    [:request-ctx-put request-ctx]))

(reframe/reg-event-db
  :request-ctx-update-success
  "update a request-ctx in request-ctx-stash after success"
  (fn [db [_ request-id response]]
       (let [request-ctx-stash (get db :request-ctx-stash {})
             request-ctx (get request-ctx-stash request-id)]
         (util/xxdp "update-success" (if-not request-ctx
           db
           (let [request-ctx (-> request-ctx
                     (assoc :pending false)
                     (assoc :success true)
                     (assoc :response response))]
             (->> request-ctx
                  (assoc request-ctx-stash request-id)
                  (assoc db :request-ctx-stash))))))))

(defn update-success
  [request-id response]
  (reframe/dispatch
    [:request-ctx-update-success request-id response]))

(reframe/reg-event-db
  :request-ctx-update-error
  (fn [db [_ request-id response error]]
       (let [request-ctx-stash (get db :request-ctx-stash {})
             request-ctx (get request-ctx-stash request-id)]
         (if-not request-ctx
           db
           (let [request-ctx (-> request-ctx
                     (assoc :pending false)
                     (assoc :success false )
                     (assoc :error error)
                     (assoc :response response))]
             (->> request-ctx
                  (assoc request-ctx-stash request-id)
                  (assoc db :request-ctx-stash)))))))

(defn update-error
 "update a request-ctx in request-ctx-stash after error"
  [request-id response error]
  (reframe/dispatch  
    [:request-ctx-update-error request-id response error]))

(reframe/reg-sub
  :request-ctx-get
  (fn [db [_ request-id]]
    (let [request-ctx-stash (get db :request-ctx-stash {})]
      (get request-ctx-stash request-id))))

(defn fetch
  "fetch a request-ctx from request-ctx-stash (nil if not found)"
  [request-id]
  (reframe/subscribe 
    [:request-ctx-get request-id] ))
