(ns supervisor.unit.search-bar
 (:require
   [reagent.core :as reagent]
   [re-frame.core :as reframe]
   [spade.core :as spade]
   [oops.core :as oops]
   [clojure.string :as string]
   [supervisor.side.qdb :as qdb]
   [supervisor.data.theme :as d-theme]
   [supervisor.data.search-bar :as d-search-bar]
   [supervisor.style :as style]
   [supervisor.util :as util]
   [supervisor.fake :as fake]
   [supervisor.space :as s]
   [supervisor.base :as b]) )

(spade/defclass css-search-bar
  []
  [:&
   [:input
    {:width (style/calc :90% :- :5px)
     :float :left
     :margin-right :5px
     }]
   [:button
    {:width :10%
     :height :100%
     :border [[:2px :solid :red]]
     :outline :none
     :float :left}
    (style/mixin-button-color (style/opacify 0.5 "#ffafaf"))
    ]])


(defn- handle-search-term-change
  [search-term event]
  (let [value (oops/oget event "target.value")]
    (d-search-bar/update-term search-term value)))

(defn- handle-clear-click
  [search-term ]
  (d-search-bar/update-term search-term ""))

(defn unit
  "search-bar"
  [props]
  (fn []
    (let [search-term (:search-term props)
          search-value @(d-search-bar/fetch-term search-term)]
    [s/box (style/merge-props props (style/tag [:search-bar (css-search-bar)]))
     [b/Input
      {:type :text
       :on-change (partial handle-search-term-change search-term)
       :placeholder (str "search " (name search-term))
       :value search-value}]
     [b/ButtonDebug
      {:on-click (partial handle-clear-click search-term)}
      "clear"]
     [s/clearfix]
     ])))
