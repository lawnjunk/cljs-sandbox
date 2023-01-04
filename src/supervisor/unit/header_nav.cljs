(ns supervisor.unit.header-nav
  (:require
    [supervisor.util :as util]
    [spade.core :as spade]
    [supervisor.style :as style]
    [supervisor.data.theme :as d-theme]
    [supervisor.data.route :as d-route]
    [supervisor.part.item-header-nav :as item-header-nav]
    ))

(spade/defclass css-header-nav []
  (let [theme @(d-theme/fetch)
        pallet (:pallet theme)]
    [:&
     {:background :none
      :height :100%
      :display :inline-block
      :float :left}
     [:nav {:padding-top :8px}
      ]]))

(defn unit
  "header-nav
   creates nav links for all routs in supervisor.dat.route/route-list
   where :show-as-nav-link is true"
  [props]
  (let [pallet @(d-theme/fetch-pallet)
        current-route @(d-route/fetch)]
  [:div (style/merge-props props {:class (css-header-nav )})
    [:nav
     (->> d-route/route-list
         (filter :show-as-nav-link)
         (map (partial item-header-nav/part current-route pallet)))
     ]]))

