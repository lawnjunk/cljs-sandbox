(ns sandbox.unit.spinner-list
  (:require 
    [spade.core :as spade]
    [reagent.core :as reagent]
    [sandbox.util :as util]
    [sandbox.unit.spinner :refer [unit-spinner]]))

(spade/defclass css-spinner-list []
  {:background "yellow"}
  [:input
   {:background "white"
    :width "100%"}])

; TODO debounce the input
(defn handle-input-change
  [state e]
  (reset! state (util/clamp 0 200 (-> e .-target .-value))))

; TODO make a spinner list
; * number input controlls item count
; TODO instead of using reagent/atom use a simple-store
(defn unit-spinner-list [count]
  (let [count (reagent/atom (util/clamp 1 200 (or count  0)))]
    (fn []
      [:div.spinner-list {:class (css-spinner-list)}
       [:input
        {:on-change (partial handle-input-change count)
         :type :number
         :min 1
         :value @count}]
        [:div.item-container
          (reverse (for [x (range 0 (util/clamp-min 1 @count))]
            [:div {:key (str "spinner-" x)}
              [unit-spinner]
              ]))]])))
