(ns supervisor.book.story-pomelo-metadata-banner
  (:require
    [supervisor.http.pomelo-metadata-fetch :as pomelo-metadata-fetch]
    [supervisor.style :as style]
    [re-frame.core :as reframe]
    [reagent.core :as reagent]
    [supervisor.util :as util]
    [supervisor.space :as s]
    [supervisor.base :as b]
    [supervisor.unit.pomelo-metadata-banner :as pomelo-metadat-banner]
    ))

(defn story-pomelo-metadata-banner
  []
  [s/box
   [b/About {}
    [:h2 "unit-story-metadata-banner"]]
   [pomelo-metadat-banner/unit]
   ])
