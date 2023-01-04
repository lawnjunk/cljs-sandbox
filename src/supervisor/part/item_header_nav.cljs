(ns supervisor.part.item-header-nav
  (:require
    [supervisor.util :as util]
    [spade.core :as spade]
    [supervisor.style :as style]
    [supervisor.data.theme :as d-theme]
    [supervisor.base :as <>]))

(spade/defclass css-item-header-nav
  [pallet]
  [:&
   {:margin-right :10px
    :color (:white pallet)
    :padding [[:2px :5px]]}
   (style/mixin-button-color
     (:header-bg pallet)
    (style/lighten 10 (:header-bg pallet) ))
   ])

(defn part
  "item-header-nav

  current-route: current state of d-route/fetch

  route-data: a spec from supervisor.data.route
  with :link-href and :link-name"
  [current-route pallet route-data ]
  (let [{:keys [link-href link-name tag page]} route-data
        is-selected (= (:page current-route) page)]
    [<>/Hpush
     {:href link-href
      :class (style/css-class (css-item-header-nav pallet) {:selected is-selected})
      :key (str "nav-link-" tag)}
     link-name]
   ))

