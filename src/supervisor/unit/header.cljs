(ns supervisor.unit.header
  (:require
    [spade.core :as spade]
    [garden.units :as units]
    [supervisor.util :as util]
    [supervisor.base :as <>]
    [supervisor.style :as style]
    [supervisor.data.theme :as d-theme]
    [supervisor.unit.header-request-pending-icon :refer [unit-header-request-pending-icon]]))

; TODO add media queries
(spade/defclass css-header-nav [theme]
  (let [pallet {:pallet theme}]
    [:&
     {:background :none
      :height :100%
      :display :inline-block
      :float :left}
     [:nav {:padding-top :8px}
      [:a {:margin-right :10px
           :background :green
           :color :white
           :padding [[:2px :5px]]}
       [:&.selected {:background :orange}]
       [:&:hover :&:focus {:background :red}]
       [:&:active {:background :black}]]]]))

; TODO make .seleced appear if route matches window location
(defn unit-header-nav []
  (let [theme @(d-theme/fetch)]
  [:div {:class (css-header-nav theme)}
    [:nav
     [<>/Hpush {:href "/"} "home"]
     [<>/Hpush {:href "/storybook"} "storybook"]
     [<>/Hpush {:href "/storybook/counter"} "counter"]
     ]]))

(spade/defclass css-header [theme]
  (let [pallet (:pallet theme)
        header-height (get-in theme [:size :header-height])]
    {:background (:header-bg pallet)
     :width :100%
     :height header-height}))

(defn unit-header []
  (let [theme @(d-theme/fetch)]
    [:header {:class (css-header theme)}
     [unit-header-request-pending-icon]
     [unit-header-nav]
     ]))
