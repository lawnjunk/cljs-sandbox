(ns supervisor.part.item-changelog
  (:require
    (spade.core :as spade)
    [supervisor.part.code-highlight :as code-highlight]
    [supervisor.util :as util]
    [supervisor.style :as style]))

(spade/defclass css-part-item-changlelog
  [theme is-lucky]
  (let [pallet (:pallet theme)
        error-color (:broken pallet)
        error-color (style/opacify 0.1 error-color)
        unlucky-css (if (not is-lucky)
                    {:background [[error-color "!important"]]} {})]
    [:&
     [:.code-highlight
      unlucky-css
      [:pre :span :code
      unlucky-css]]
      ]))

(defn part
  [theme changelog-data]
  (let [version (get changelog-data :version "unknown")
        is-lucky (get changelog-data :isLucky "unknown")
        build-note (get changelog-data :buildNote "no build note") ]
  [:div (merge  (style/tag (css-part-item-changlelog theme is-lucky)) {:key (str "changelog-item-" version)})
   [:h2 version]
   [code-highlight/part :markdown build-note]]))
