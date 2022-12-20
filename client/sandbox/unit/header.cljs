(ns sandbox.unit.header
  (:require
    [spade.core :as spade]
    [sandbox.data.request-metrix :as rmetrix]
    [garden.units :as units]
    [sandbox.util :as util]
    [sandbox.style :as style]
    [sandbox.base :as <>]
    [sandbox.unit.header-request-pending-icon :refer [unit-header-request-pending-icon]]))

; TODO make header fixed (top) and app container fixed below

; TODO add media queries
(spade/defclass css-header-nav []
  {
   :background :none
   :height :100%
   :display :inline-block
   :float :left
   }
  [:nav {:padding-top :8px}
    [:a {:margin-right :10px
         :background :green
         :color :white
         :padding [[:2px :5px]]} 
     [:&.selected {:background :orange}]
     [:&:hover :&:focus {:background :red}]
     [:&:active {:background :black}]]
    ])

; TODO make .seleced appear if route matches window location
(defn unit-header-nav []
  [:div {:class (css-header-nav)} 
    [:nav 
     [:a.selected {:href "/#/"} "home"]
     [:a {:href "/#/storybook"} "storybook"]
     [:a {:href "/#/storybook/counter"} "counter"]
     ]])

(spade/defclass css-header []
  {:background (:header-bg @style/pallet)
   :width :100%
   :height style/size-px-header-height })

(defn unit-header []
  [:header {:class (css-header)}
   [unit-header-request-pending-icon] 
   [unit-header-nav]
   ])
