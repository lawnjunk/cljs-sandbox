(ns supervisor.unit.changelog
  (:require
    [spade.core :as spade]
    [reagent.core :as reagent]
    [oops.core :as oops]
    [re-frame.core :as reframe]
    [clojure.string :as string]
    [supervisor.side.api :as api]
    [supervisor.side.ldb :as ldb]
    [supervisor.side.qdb :as qdb]
    [supervisor.http.pompom-changelog :as http-pompom-changelog]
    [supervisor.data.request-ctx :as d-request-ctx]
    [supervisor.data.search-bar :as d-search-bar]
    [supervisor.data.changelog :as d-changelog]
    [supervisor.data.theme :as d-theme]
    [supervisor.unit.blue-dot :as blue-dot]
    [supervisor.part.request-ctx-error-card :as request-ctx-error-card]
    [supervisor.part.code-highlight :as code-highlight]
    [supervisor.unit.search-bar :as search-bar]
    [supervisor.style :as style]
    [supervisor.util :as util]
    [supervisor.space :as s]))

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

(defn part_item_changelog
  [theme changelog-data]
  (let [version (get changelog-data :version "unknown")
        is-lucky (get changelog-data :isLucky "unknown")
        build-note (get changelog-data :buildNote "no build note") ]
  [:div (merge  (style/tag (css-part-item-changlelog theme is-lucky)) {:key (str "changelog-item-" version)})
   [:h2 version]
   [code-highlight/part :markdown build-note]]))


(defn unit
  "unit-changelog"
  []
  (let [request-id (util/id-atom)
        search-bar-term :changelog]
    (http-pompom-changelog/request @request-id)
    (fn []
      (let [theme @(d-theme/fetch)
            request-ctx @(d-request-ctx/fetch @request-id)
            filter-term @(d-search-bar/fetch-term search-bar-term)
            pending (:pending request-ctx)
            changelog @(d-changelog/fetch)
            title (if pending "CHANGELOG LOADING..." "CHANGELOG")
            filter-by (str (or filter-term ""))
            total-count (count changelog)
            changelog (filter #(or (util/fuzzy-match? (str (:version %)) filter-by)
                                   (util/fuzzy-match? (str (:buildNote %)) filter-by))
                              changelog)
            showing-count (count changelog)]
        [s/box
         [:h1 title]
         (when changelog [blue-dot/unit {:data changelog}])
         [search-bar/unit {:search-term search-bar-term}]
         [:p filter-term]
         [request-ctx-error-card/part request-ctx]
         (when changelog
           [:p.counter "count: " total-count " showing: " showing-count])
         [:div (style/tag :changlog-item-container)
           (map (partial part_item_changelog theme) changelog)]
         ]))))

