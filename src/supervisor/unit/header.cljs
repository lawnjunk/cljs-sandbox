(ns supervisor.unit.header
  (:require
    [spade.core :as spade]
    [supervisor.util :as util]
    [supervisor.base :as <>]
    [supervisor.style :as style]
    [supervisor.data.theme :as d-theme]
    [supervisor.data.route :as d-route]
    [supervisor.unit.header-request-pending-icon :refer [unit-header-request-pending-icon]]))

; TODO add media queries
(spade/defclass css-header-nav []
  (let [theme @(d-theme/fetch)
        pallet (:pallet theme)]
    [:&
     {:background :none
      :height :100%
      :display :inline-block
      :float :left}
     [:nav {:padding-top :8px}
      [:a {:margin-right :10px
           :color :white
           :padding [[:2px :5px]]}
       (style/mixin-button-color
         (:header-bg pallet)
         (style/lighten (:header-bg pallet) 10)
         )
       ]]]))

(defn unit-header-nav-item
  "route data from supervisor.data.route"
  [current-route route-data]
  (let [{:keys [link-href link-name tag page]} route-data
        is-selected (= (:page current-route) page)]
    [<>/Hpush
     {:href link-href
      :class (style/css-class {:selected is-selected})
      :key (str "nav-link-" tag)}
     link-name]
   ))

(defn unit-header-nav []
  (let [current-route @(d-route/fetch)]
  [:div {:class (css-header-nav )}
    [:nav
     (->> d-route/route-list
         (filter :show-as-nav-link)
         (map (partial unit-header-nav-item current-route)))
     ]]))

(spade/defclass css-header []
  (let [theme @(d-theme/fetch)
        pallet (:pallet theme)
        header-height (get-in theme [:size :header-height])]
    {:background (:header-bg pallet)
     :width :100%
     :height header-height}))

(defn unit-header
  []
  [:header {:class (css-header)}
   [unit-header-request-pending-icon]
   [unit-header-nav]
   ])
