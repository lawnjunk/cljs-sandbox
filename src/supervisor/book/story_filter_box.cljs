(ns supervisor.book.story-filter-box
  (:require
    [spade.core :as spade]
    [supervisor.style :as style]
    [supervisor.util :as util]
    [supervisor.base :as b]
    [supervisor.space :as s]))

(defn story
  "story-filter-box"
  []
  [s/box (style/tag [:full-height])
   [b/About (style/tag [:gap-down])
    [:h1 "unit-filter-box"]]])
