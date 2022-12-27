(ns sandbox.unit.counter
  (:require
    [spade.core :refer [defclass]]
    [re-frame.core :as reframe]
    [sandbox.data.query :as query]
    [sandbox.util :as util]
    [sandbox.base :as <>]))

; TODO siik counter mods
; * toggle auto
; * auto speed control
; * increment control
; * if-not is-auto? maunal up/down
; * counter-inc should use a
; * saturation/light controll

(reframe/reg-sub
  :counter
  (fn [db] (get db :counter)))

(reframe/reg-event-db
  :set-counter
  (fn [db [_ value]]
    (assoc db :counter value)))

(reframe/reg-event-db
  :inc-counter
  (fn [db [_ data]]
    (assoc db :counter (+ (get db :counter ) data))))

(defn counter-inc-random
  []
  (reframe/dispatch
    [:inc-counter (js/Math.random)]))

(defclass css-counter-color [num]
  {:background  (str "hsl(" (abs (mod num 360)) ",96%,70%)")})

(defonce counter-interval  (js/setInterval #(counter-inc-random) 5))

(defn unit-counter []
  (let [counter @(reframe/subscribe [:counter])
        do-dec #(reframe/dispatch [:set-counter  (- counter 1)])
        counter-hue (js/Math.floor (abs (mod counter 360)))]
    [:div {:class (css-counter-color (if (number? counter-hue) counter-hue 0))}
     [<>/Button {:on-click #(counter-inc-random)} "increment"] 
     [<>/Button {:on-click #(query/write-data! {:name (util/id-gen)})} "decriment"]
     [:p "rand counter: " counter]
     [:p "hue: " counter-hue]]))
