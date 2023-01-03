(ns supervisor.style
  (:require
    [spade.core :as spade]
    [garden.color :as gcolor]
    [garden.units :as units]
    [clojure.string :as s]
    [reagent.core :as reagent]
    [supervisor.util :as util]))

(defn as-color
  [value]
  (if (keyword? value)
    (name value)
    value))

(defn lighten
  "lighten a color with a parsable number amount"
  [color amount]
  (gcolor/lighten color (util/parse-number amount)))

(defn darken
  "darken a color with a parsable number amount"
  [color amount]
  (gcolor/darken color (util/parse-number amount)))

(defn opacify
  "opacify a color with a parsable number amount"
  [color amount]
  (gcolor/opacify color (util/parse-number amount)))

; px size
(defn px-val
  [value]
  (if (map? value)
    (:magnitude val)
    (util/parse-int value)))

(defn px
  "create a garden.units/px from a parable int"
  [value]
  (units/px (util/parse-int value)))

(defn- create-px-math
  "create a px arithmatic fn that works with keywords strings and nums and px"
  [f]
  (fn [a b]
    (px (f (px-val a) (px-val b)))))

(def px-sub (create-px-math -))
(def px-add (create-px-math +))
(def px-mul (create-px-math *))
(def px-div (create-px-math /))
(def px-mod (create-px-math mod))


(defn mixin-button-color
  "style the colors for a button like element
  including :focus :hover :active :disabled

  adding a .selected class will use the selected-bg/fg
  insdead of primary
  "
  ([primary-bg]
   (mixin-button-color
     primary-bg
     "#000000"
     "#000000"
     "#000000"
     "#999999"
     ))
  ([primary-bg selected-bg]
   (mixin-button-color
     primary-bg
     "#000000"
     selected-bg
     "#000000"
     "#999999"
     ))
  ([primary-bg primary-fg selected-bg selected-fg disabled-fg]
  [:& {:background primary-bg
       :color primary-fg
       :cursor :pointer }
   [:&:focus
    {:background (darken primary-bg 5)}]
   [:&:hover
    {:background (darken primary-bg 10)} ]
   [:&:active
    {:background (darken primary-bg 15)} ]
   [:&:disabled
    {:background  (lighten primary-bg 5)
     :color disabled-fg
     :cursor :auto}]
   [:&.seleced
    {:background selected-bg
     :color selected-fg}
    [:&:focus
     {:background (darken selected-bg 5)}]
    [:&:hover
     {:background (darken selected-bg 10)} ]
    [:&:active
     {:background (darken selected-bg 15)} ]
    [:&:disabled
     {:background  (lighten selected-bg 5)
      :color disabled-fg
      :cursor :auto
      }]
    ]])
  )
