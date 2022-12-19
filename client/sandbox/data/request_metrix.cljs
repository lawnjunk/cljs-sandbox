(ns sandbox.data.request-metrix
  (:require
    [re-frame.core :as reframe]
    [sandbox.util :as util]))

; keep track about all metrix about present/past request

(def request-metrix-default 
  {:total 0
   :pending 0
   :success 0
   :abort 0
   :error 0})

(defn dec-pending 
  [pending]
  (util/clamp-min (dec pending) 0))

(reframe/reg-event-db
  :request-metrix-request-new
  (fn [db] 
    (let [total (get-in db [:request-metrix :total] 0)
          pending (get-in db [:request-metrix :pending] 0)]
      (-> db
      (assoc-in [:request-metrix :total] (inc total))
      (assoc-in [:request-metrix :pending] (inc pending))))))

(reframe/reg-event-db
  :request-metrix-request-success
  (fn [db] 
    (let [success (get-in db [:request-metrix :success] 0)
          pending (get-in db [:request-metrix :pending] 0)]
      (-> db
        (assoc-in [:request-metrix :success] (inc success))
        (assoc-in [:request-metrix :pending] (dec-pending pending))))))

(reframe/reg-event-db
  :request-metrix-request-error
  (fn [db] 
    (let [error (get-in db [:request-metrix :error] 0)
          pending (get-in db [:request-metrix :pending] 0)]
      (-> db
        (assoc-in [:request-metrix :error] (inc error ))
        (assoc-in [:request-metrix :pending] (dec-pending pending))))))

(reframe/reg-event-db
  :request-metrix-request-abort
  (fn [db] 
    (let [abort (get-in db [:request-metrix :abort] 0)
          pending (get-in db [:request-metrix :pending] 0)]
      (-> db
      (assoc-in [:request-metrix :abort] (inc abort))))))

(reframe/reg-event-db
  :request-metrix-request-abort-and-delete
  (fn [db] 
    (let [abort (get-in db [:request-metrix :abort] 0)
          pending (get-in db [:request-metrix :pending] 0)]
      (-> db
      (assoc-in [:request-metrix :abort] (inc abort))
      (assoc-in [:request-metrix :pending] (dec-pending pending))))))

(reframe/reg-sub 
  :request-metrix-fetch
  (fn [db]
    (get db :request-metrix request-metrix-default)))

(defn fetch
  []
  (reframe/subscribe [:request-metrix-fetch]))
