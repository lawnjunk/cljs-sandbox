(ns sandbox.data.request-ctx
  (:require
    [ajax.core :as cljs-ajax]
    [re-frame.core :as reframe]
    [sandbox.util :as util]
    [sandbox.data.request-metrix]))

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
    (create url (util/id-gen) nil nil))
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

(reframe/reg-event-fx
  :request-ctx-abort
  (fn [world [_ request-id]]
    (let [db (:db world)
          request-ctx-stash (db-get-request-ctx-stash db)
          request-ctx (get request-ctx-stash request-id)]
      (if (and request-ctx (:pending request-ctx)) 
        (let [ajax (get request-ctx :ajax)]
          (.abort ajax)))
      {:dispatch [:request-metrix-request-abort]})))

(defn abort [request-id]
  (reframe/dispatch [:request-ctx-abort request-id]))

(reframe/reg-event-fx
  :request-ctx-abort-and-delete
  (fn [world [_ request-id]]
    (let [db (:db world)
          request-ctx-stash (db-get-request-ctx-stash db)
          request-ctx (get request-ctx-stash request-id)
          new-request-ctx-stash (dissoc request-ctx-stash request-id)]
      (if (and request-ctx (:pending request-ctx))
        (let [ajax (get request-ctx :ajax)]
          (.abort ajax)))
      {:db (assoc db :request-ctx-stash new-request-ctx-stash)
       :dispatch [:request-metrix-request-abort-and-delete]})))

(defn abort-and-delete [request-id]
  (reframe/dispatch [:request-ctx-abort-and-delete request-id]))

(reframe/reg-event-fx 
  :request-ctx-put
  (fn [cofx [_ request-ctx]]
    (let [db (:db cofx)
          request-id (:request-id request-ctx)]
      {:db (assoc-in db [:request-ctx-stash request-id] request-ctx)
       :dispatch [:request-metrix-request-new]}
      )))

(defn put
  "put a new request-ctx into the request-ctx-stash"
  [request-ctx]
  (reframe/dispatch
    [:request-ctx-put request-ctx]))

; TODO -> update db :request-count
(reframe/reg-event-fx
  :request-ctx-update-success
  "update a request-ctx in request-ctx-stash after success"
  (fn [cofx [_ request-id res-data error]]
       (let [db (:db cofx)
             request-ctx-stash (db-get-request-ctx-stash db)
             request-ctx (get request-ctx-stash request-id)]
         (if request-ctx
           {:dispatch [:request-metrix-request-success]
            :db (let [request-ctx (-> request-ctx
                     (assoc :pending false)
                     (assoc :is-success true)
                     (assoc :error error)
                     (assoc :res-data res-data))] 
                  (->> request-ctx 
                       (assoc request-ctx-stash request-id) 
                       (assoc db :request-ctx-stash)))}))))

(defn update-success
  ([request-id res-data]
   (update-success request-id res-data nil))
  ([request-id res-data error]
    (reframe/dispatch
      [:request-ctx-update-success request-id res-data error])))

; TODO -> update db :request-count
(reframe/reg-event-fx
  :request-ctx-update-error
  (fn [cofx [_ request-id res-data error]]
       (let [db (:db cofx)
             request-ctx-stash (get db :request-ctx-stash {})
             request-ctx (get request-ctx-stash request-id)]
         (if request-ctx
           {:dispatch [:request-metrix-request-error]
            :db (let [request-ctx (-> request-ctx
                     (assoc :pending false)
                     (assoc :is-success false )
                     (assoc :error error)
                     (assoc :res-data res-data))] 
                  (->> request-ctx 
                       (assoc request-ctx-stash request-id) 
                       (assoc db :request-ctx-stash)))}))))

(defn update-error
 "update a request-ctx in request-ctx-stash after error"
  [request-id res-data error]
  (reframe/dispatch
    [:request-ctx-update-error request-id res-data error]))

(reframe/reg-sub
  :request-ctx-fetch
  (fn [db [_ request-id]]
    (let [request-ctx-stash (get db :request-ctx-stash {})]
      (get request-ctx-stash request-id))))

(defn fetch
  "fetch a request-ctx from request-ctx-stash (nil if not found)"
  [request-id]
  (reframe/subscribe 
    [:request-ctx-fetch request-id] ))
