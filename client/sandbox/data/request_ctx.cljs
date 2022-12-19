(ns sandbox.data.request-ctx
  (:require
    [ajax.core :as cljs-ajax]
    [re-frame.core :as reframe]
    [sandbox.util :as util]))

; db 
;   :request-context-stash 
;      request-id : req-ctx
;   :request-count
;      :total (total number of requests) made
;      :pending (total number of request pending)
;      :success 
;      :fail

(defn create
  ([url]
    (create url (util/genid) nil nil))
  ([url request-id req-data ajax]
    {:request-id request-id
     :ajax ajax
     :url url
     :pending true
     :req-data req-data
     :res-data nil
     :error nil
     :is-success nil}))

(defn- db-get-request-ctx-stash [db]
  (get db :request-ctx-stash {}))

(reframe/reg-event-db
  :request-ctx-update-ajax
  (fn [db [_ request-id ajax]]
    (println "set ajaz " request-id ajax)
    (let [request-ctx-stash (db-get-request-ctx-stash db)
          request-ctx (get request-ctx-stash request-id)]
      (if-not request-ctx db
        (->> (assoc request-ctx :ajax ajax)
             (assoc request-ctx-stash request-id)
             (assoc db :request-ctx-stash))))))

(reframe/reg-event-fx
  :request-ctx-abort
  (fn [world [_ request-id]]
    (println "abort requset" request-id)
    (let [db (:db world)
          request-ctx-stash (db-get-request-ctx-stash db)
          request-ctx (get request-ctx-stash request-id)]
      (if request-ctx
        (let [ajax (get request-ctx :ajax)]
          (.abort ajax))))))

(defn abort [request-id]
  (reframe/dispatch [:request-ctx-abort request-id]))

(reframe/reg-event-db
  :request-ctx-put
  (fn [db [_ request-ctx]]
       (let [request-id (:request-id request-ctx)
             request-ctx-stash (db-get-request-ctx-stash db)]
         (util/xxdp "request-ctx-put" (->> request-ctx
              (assoc request-ctx-stash request-id)
              (assoc db :request-ctx-stash))))))

(defn put
  "put a new request-ctx into the request-ctx-stash"
  [request-ctx]
  (reframe/dispatch
    [:request-ctx-put request-ctx]))

; TODO -> update db :request-count
(reframe/reg-event-db
  :request-ctx-update-success
  "update a request-ctx in request-ctx-stash after success"
  (fn [db [_ request-id res-data error]]
       (let [request-ctx-stash (db-get-request-ctx-stash db)
             request-ctx (get request-ctx-stash request-id)]
         (if-not request-ctx db
           (let [request-ctx (-> request-ctx
                     (assoc :pending false)
                     (assoc :is-success true)
                     (assoc :error error)
                     (assoc :res-data res-data))]
             (->> request-ctx
                  (assoc request-ctx-stash request-id)
                  (assoc db :request-ctx-stash)))))))

(defn update-success
  ([request-id res-data]
   (update-success request-id res-data nil))
  ([request-id res-data error]
   (println "fook jee" res-data)
    (reframe/dispatch
      [:request-ctx-update-success request-id res-data error])))

; TODO -> update db :request-count
(reframe/reg-event-db
  :request-ctx-update-error
  (fn [db [_ request-id res-data error]]
       (let [request-ctx-stash (get db :request-ctx-stash {})
             request-ctx (get request-ctx-stash request-id)]
         (if-not request-ctx
           db
           (let [request-ctx (-> request-ctx
                     (assoc :pending false)
                     (assoc :is-success false )
                     (assoc :error error)
                     (assoc :res-data res-data))]
             (->> request-ctx
                  (assoc request-ctx-stash request-id)
                  (assoc db :request-ctx-stash)))))))

(defn update-error
 "update a request-ctx in request-ctx-stash after error"
  [request-id res-data error]
   (println "dang" res-data)
  (reframe/dispatch
    [:request-ctx-update-error request-id res-data error]))

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
