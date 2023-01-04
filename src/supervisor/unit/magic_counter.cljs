; this is not really a unit but it was just used for testing ideas
; (does not allow for react props)
(ns supervisor.unit.magic-counter
  (:require
    [supervisor.util :as util]
    [reagent.core :as reagent]
    [spade.core :refer [defclass]]
    [re-frame.core :as reframe]
    [supervisor.side.qdb :as qdb]
    [supervisor.base :as <>]))

(defclass css-counter-color [num]
  {:background  (str "hsl(" (abs (mod (util/clamp  0 360 num) 360)) ",96%,70%)")
   :padding :10px
   :border [[:2px :solid :black]]}
  [:p
   {:margin-top :5px}]
  [:button
   {:margin-right :10px
    :border [[:2px :solid :black]]
    }])

; APP STATE
(reframe/reg-sub
  :counter
  (fn [db] (get db :counter 0)))

(reframe/reg-event-db
  :inc-counter
  (fn [db [_ data]]
    (assoc db :counter (+ (get db :counter 0) data))))

(defn counter-inc-random
  []
  (reframe/dispatch [:inc-counter (js/Math.random)]))

(def qdb-state-default
  {:auto-inc-on true
   :auto-inc-interval 5})

; LOCAL STATE used to track when and how to rest the interval
(def a-delta-auto-inc-on (reagent/atom false))
(def a-delta-auto-inc-interval  (reagent/atom 1000))
(def a-cancel-fn (reagent/atom identity))

(defn- reset-interval [speed]
  (@a-cancel-fn)
  (reset! a-delta-auto-inc-on true)
  (reset! a-delta-auto-inc-interval  speed)
  (reset! a-cancel-fn (util/interval speed counter-inc-random)))

(defn qdb-update-interval
  [qdb speed]
  (qdb/overwrite! (assoc qdb :auto-inc-interval speed)))

(defn qdb-update-auto-inc-on
  [qdb state]
  (qdb/overwrite! (assoc qdb :auto-inc-on state)) )

(defn unit []
  (let [qdb @(qdb/fetch)]
    (reagent/create-class
      {:component-did-mount
       (fn []
          (reframe/dispatch [:inc-counter (* 1000 (js/Math.random))])
          (qdb/overwrite! (merge qdb-state-default qdb))
          (reset-interval (:auto-inc-interval qdb)))
       :component-will-unmount #(@a-cancel-fn)
       :render
       (fn []
          (let [qdb @(qdb/fetch)
                auto-inc-on (get qdb :auto-inc-on "none")
                auto-inc-interval (get qdb :auto-inc-interval 100)
                counter @(reframe/subscribe [:counter])
                counter-hue (js/Math.floor (abs (mod counter 360)))
                inc-speed (fn []
                            (qdb-update-interval
                              qdb
                              (util/clamp-max 1000 (+ auto-inc-interval 55))))
                dec-speed (fn []
                            (qdb-update-interval
                              qdb
                              (util/clamp-min 5 (- auto-inc-interval 55))))
                ]
            (when (and auto-inc-on (not= @a-delta-auto-inc-interval  auto-inc-interval))
              (reset-interval auto-inc-interval))
            (when (not= @a-delta-auto-inc-on auto-inc-on)
              (if auto-inc-on
                (reset-interval auto-inc-interval)
                (do
                  (reset! a-delta-auto-inc-on false)
                  (@a-cancel-fn))))
            [:div {:class (css-counter-color (if (number? counter-hue) counter-hue 0))}
             [<>/Button {:on-click #(qdb-update-auto-inc-on qdb (not auto-inc-on))} "toggle"]
             [<>/Button {:on-click #(inc-speed)} "increment interval"]
             [<>/Button {:on-click #(dec-speed)} "decriment interval"]
             [:p "AUTO INC: " (str auto-inc-on)]
             [:p "INTERVAL IN MS: " auto-inc-interval]
             [:p "RANDOM NUMBER: " counter]
             [:p "HSL HUE: " counter-hue]]))})))

