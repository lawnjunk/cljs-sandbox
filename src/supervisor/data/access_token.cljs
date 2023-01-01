(ns supervisor.data.access-token
  (:require 
    [re-frame.core :as reframe]
    [supervisor.side.ldb :as ldb]
    [supervisor.util :as util]))

(reframe/reg-event-fx
  ::write-access-token
  [(ldb/inject)]
  (fn [cofx [_ access-token]]
    (let [ldb (:ldb cofx)]
      (util/pp "access-token fx" access-token)
      {:ldb (assoc ldb :access-token access-token)})))

(defn write-fx
  [access-token]
  [:dispatch [::write-access-token access-token] ])

(defn write
  [access-token]
  (reframe/dispatch [::write-access-token access-token]))

(reframe/reg-sub
  ::access-token-fetch
  (ldb/signal)
  (fn [ldb]
   (:access-token ldb)))

(defn fetch []
  (reframe/subscribe [::access-token-fetch]))
