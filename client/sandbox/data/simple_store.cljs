(ns sandbox.data.simple-store
  (:require  
    [re-frame.core :as reframe]
    [clojure.string :as s]
    [sandbox.util :as util]))
(defn reg-sub-simple-store-fetch
  [thing]
  (reframe/reg-sub
    thing
    (fn [db]
      (get db thing)))) ;; dispatch

(defn reg-event-db-simple-store-set
  [set-event thing]
  (reframe/reg-event-db
    set-event
    (fn [db [_ data]]
      (assoc db thing data))))

(defn reg-event-db-simple-store-del
  [del-event thing]
  (reframe/reg-event-db
    del-event
    (fn [db []]
      (dissoc db thing))))

(defn simple-store-create
  "thing should be a :keyword that holds
   a value in the root of the global store"
  ([thing]
    (simple-store-create thing nil))
  ([thing default]
    (let [thing-set-keyword (s/replace (str thing) ":" ":set-")
          thing-del-keyword (s/replace (str thing) ":" ":del-")]
      (reg-event-db-simple-store-set thing-set-keyword thing)
      (reg-event-db-simple-store-del thing-del-keyword thing)
      (reg-sub-simple-store-fetch thing)
      (let [thing-get (fn []
                     (reframe/subscribe [thing]))
            thing-set (fn [value]
                     (reframe/dispatch [thing-set-keyword value]))
            thing-del (fn []
                     (reframe/dispatch [thing-del-keyword]))
            thing-tmp (fn [delayInMS value]
                     (thing-set value)
                     (util/wait delayInMS #(thing-del)))
            thing-mod (fn [handler]
                     (thing-set (handler (thing-get))))]
        (if-not (nil? default) (thing-set default))
      {:get thing-get
       :set thing-set
       :del thing-del
       :mod thing-mod
       :tmp thing-tmp}))))
