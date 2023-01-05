(ns supervisor.book.story-blue-dot
  (:require
    [reagent.core :as reagent]
    [spade.core :as spade]
    [supervisor.base :as b]
    [supervisor.space :as s]
    [supervisor.util :as util]
    [supervisor.fake :as fake]
    [supervisor.data.theme :as d-theme]
    [supervisor.unit.blue-dot :as blue-dot]))

(spade/defclass css-story-blue-dot []
  {:padding :10px
   :margin-bottom :10px
   }
  )

(defn story-blue-dot
  []
  [s/box
  [b/Invert {:class (css-story-blue-dot)}
    [:h2 "unit-blu-dot"]]
  [blue-dot/unit {:data (fake/data)}]
  ])
