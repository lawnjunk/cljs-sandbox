(ns supervisor.book.story-changelog
  (:require
    [spade.core :as spade]
    [reagent.core :as reagent]
    [re-frame.core :as reframe]
    [clojure.string :as string]
    [supervisor.side.api :as api]
    [supervisor.side.ldb :as ldb]
    [supervisor.side.qdb :as qdb]
    [supervisor.data.request-ctx :as d-request-ctx]
    [supervisor.data.theme :as d-theme]
    [supervisor.style :as style]
    [supervisor.util :as util]
    [supervisor.fake :as fake]
    [supervisor.space :as s]
    [supervisor.base :as b]
    [supervisor.unit.changelog :as changelog]
   ))
(spade/defclass css-story-changelog
  []
  [:.changelog
   {:height (style/calc :100% :- :70px)}])

(defn story
  []
  [s/box (style/tag [:full-tall (css-story-changelog)])
   [b/About {}
    [:h1 :unit-changelog]]
   [changelog/unit]])



