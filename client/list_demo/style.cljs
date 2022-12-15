(ns list-demo.style
  (:require 
    [garden.selectors :refer [specificity]]
    [reagent.core :as reagent]))

(def color (reagent/atom
  {:container-bg " #deddda"
   :container-fg "#000"
   :button-main "#B9C09E"
   :button-focus "#a7aa98"
   :button-active "#979b85"
   }))



