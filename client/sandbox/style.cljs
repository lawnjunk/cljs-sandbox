(ns sandbox.style
  (:require
    [spade.core :as spade]
    [garden.color :as color]
    [garden.units :as units]
    [clojure.string :as s]
    [reagent.core :as reagent]
    [sandbox.util :as util]))

(def size-px-header-height (units/px 40))

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
   :storybook-panel "#6c8468"
   :pending-1 "#3a6f72"
   :pending-2 "#6c9684"
   :header-bg "#97ad93"
   }))
