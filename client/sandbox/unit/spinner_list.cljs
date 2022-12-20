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
  (reset! state (util/clamp (-> e .-target .-value) 0 200)))

; TODO make a spinner list
; * number input controlls item count
(defn unit-spinner-list [count]
  (let [count (reagent/atom (util/clamp (or count  0) 1 200))]
    (fn []
      [:div.spinner-list {:class (css-spinner-list)}
       [:input 
        {:on-change (partial handle-input-change count)
         :type :number
         :min 1
         :value @count}]
        [:div.item-container
          (reverse (for [x (range 0 (util/clamp-min @count 1))]
            [:div {:key (str "spinner-" x)}
              [unit-spinner]
              ]))]])))
