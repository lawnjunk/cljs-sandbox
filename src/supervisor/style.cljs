(ns supervisor.style
  (:require
    [spade.core :as spade]
    [garden.color :as color]
    [garden.units :as units]
    [clojure.string :as s]
    [reagent.core :as reagent]
    [supervisor.util :as util]))

(def size-px-header-height (units/px 40))
(def size-px-main-height (util/css-calc :100vh :- size-px-header-height))

(def breakpoint
  {:mobile 600
   :tablet 900})

(def pallet (reagent/atom
  {:container-bg " #deddda"
   :bg " #deddda"
   :container-fg "#000"
   :fg "#000"
   :button-main "#B9C09E"
   :button-focus "#a7aa98"
   :button-active "#979b85"
   :button-selected-bg "#62635d"
   :button-selected-fg "#B9C09E"
   :storybook-panel "#74b287"
   :storybook-nav-unselected "#4da568"
   :storybook-nav-selected "#c0db97"
   :pending-1 "#3a6f72"
   :pending-2 "#6c9684"
   :header-bg "#68cc86"
   }))
