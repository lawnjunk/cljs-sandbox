(ns supervisor.page.loading
  (:require
    [spade.core :as spade]
    [supervisor.data.theme :as theme]))

(spade/defclass css-page-landing [theme]
  (let [pallet (:pallet theme)]
    [:&
     {:width :100%
      :height :100%
      :background (:bg pallet)}
     [:h1
      {:padding-top :100px
       :text-align :center}]]))

(defn page-loading []
  (let [css-theme @(theme/fetch)]
    [:div {:class (css-page-landing css-theme)}
     [:h1  "loading..." ]]))
