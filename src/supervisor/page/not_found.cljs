(ns supervisor.page.not-found
  (:require
    [supervisor.util :as util]
    [spade.core :as spade]
    [supervisor.base :as <>]
    [supervisor.data.theme :as theme]))

; TODO make a game for this page?
(spade/defclass css-page-landing [theme]
  (let [pallet (:pallet theme)]
    [:&
     {:width :100%
      :height :100%
      :background (:bg pallet)}
     [:h1
      {:padding-top :100px
       :text-align :center}]
     [:a
      {:display :block
       :width :100%
       :font-size :25px
       :text-align :center
       :color :blue}
      ]]))

; TODO use history api to make << home (<<< back)
(defn page
  "page not found"
  []
  (let [css-theme @(theme/fetch)]
    [:div {:class (css-page-landing css-theme)}
     [:h1  "404: page not found" ]
     [<>/Hpush {:href "/"} "<<home"]]))
