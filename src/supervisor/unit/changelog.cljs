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
    [supervisor.part.item-changelog :as item-changelog]
    [supervisor.unit.search-bar :as search-bar]
    [supervisor.style :as style]
    [supervisor.util :as util]
    [supervisor.space :as s]))

(spade/defclass css-changelog
  []
  (let [pallet @(d-theme/fetch-pallet)
        border-color (:storybook-panel pallet)]
    [:&
     {:position :relative
      :height (style/calc :100% :- :55px)
      }
     [:.counter
      {:color border-color}]
     [:.blue-dot
      {:position :absolute
       :top :5px
       :right :5px}]
     [:.changlog-item-container
      {:height (style/calc  :100% :- :100px)
       :overflow :scroll
       :border-bottom [[:5px :solid border-color]]
       }
       [:pre
        {:overflow :scroll }]

      ]
     ]))

(defn unit
  "unit-changelog"
  [props]
  (let [request-id (util/id-atom)
        search-bar-term :changelog]
    (http-pompom-changelog/request @request-id)
    (fn []
      (let [theme @(d-theme/fetch)
            request-ctx @(d-request-ctx/fetch @request-id)
            filter-term @(d-search-bar/fetch-term search-bar-term)
            pending (:pending request-ctx)
            changelog @(d-changelog/fetch)
            title (if pending "CHANGELOG LOADING..." "CHANGE_LOG")
            filter-by (str (or filter-term ""))
            total-count (count changelog)
            changelog (filter #(or (util/fuzzy-match? (str (:version %)) filter-by)
                                   (util/fuzzy-match? (str (:buildNote %)) filter-by))
                              changelog)
            showing-count (count changelog)]
        [s/box
         (style/merge-props
           (style/tag [:changelog (css-changelog)])
           props)
         [:h1 title]
         (when changelog [blue-dot/unit {:data changelog}])
         [search-bar/unit {:search-term search-bar-term}]
         [request-ctx-error-card/part request-ctx]
         (when changelog
           [:p.counter "count: " total-count " showing: " showing-count])
         [:div (style/tag :changlog-item-container)
           (map (partial item-changelog/part theme) changelog)]
         ]))))

