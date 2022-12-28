(ns supervisor.page.loading
  (:require
    [spade.core :as spade]
    [supervisor.style :as style]))

(spade/defclass css-page-landing []
  {:width :100%
   :height :100%
   :background (:bg @style/pallet)
   }
   [:h1
    {:padding-top :100px
     :text-align :center}])

(defn page-loading []
  [:div {:class (css-page-landing)}
   [:h1  "loading..." ]])
