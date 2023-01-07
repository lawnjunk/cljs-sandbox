(ns supervisor.book.story-search-bar
  (:require
    [supervisor.util :as util]
    [supervisor.unit.search-bar :as search-bar]
    [supervisor.data.search-bar :as d-search-bar]
    [supervisor.style :as style]
    [supervisor.base :as b]
    [supervisor.space :as s]))

(defn story
  "story-search-bar"
  []
  (let [cat-search-term :cat
        cat-search-value @(d-search-bar/fetch-term cat-search-term)
        dog-search-term :dog
        dog-search-value @(d-search-bar/fetch-term dog-search-term)]
    [s/box (style/tag [:full-height ])
     [b/About (style/tag :gap-down)
      [:h1 "unit-search-bar"]
      [:p "uses the query string for state manament ^ look up and check it out :)"]
      [:p "each search bar has a uniq term so more than one can be used at a time"]
      [:p "cat value: " cat-search-value]
      [:p "dog valule: " dog-search-value]]
     [search-bar/unit
      (style/merge-props
        {:search-term cat-search-term}
        (style/tag [:gap-down])
        )]

     [search-bar/unit {:search-term dog-search-term}]

     ]))
