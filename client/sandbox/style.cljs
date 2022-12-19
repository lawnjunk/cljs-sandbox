(ns sandbox.style
  (:require 
    [reagent.core :as reagent]))

; TODO make color a reframe thang

(def color (reagent/atom
  {:container-bg " #deddda"
   :container-fg "#000"
   :button-main "#B9C09E"
   :button-focus "#a7aa98"
   :button-active "#979b85"
   :button-selected-bg "#62635d"
   :button-selected-fg "#B9C09E"
   :peach "#FC9A69"
   }))



