; utils for working with css
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
  (gcolor/lighten (as-color color) (util/parse-number amount)))


(defn darken
  "darken a color with a parsable number amount"
  [color amount]
  (gcolor/darken (as-color color) (util/parse-number amount)))

(defn opacify
  "opacify a color with a parsable number amount"
  [color amount]
  (gcolor/opacify (as-color color) (util/parse-number amount)))

; px size
(defn px-val
  [value]
  (if (units/unit? value)
    (:magnitude value)
    (util/parse-int value)))

(defn px
  "create a garden.units/px from anything that can util/parse-int"
  [size]
  (units/px (util/parse-number size)))

(defn- create-px-math
  "create a px arithmatic
   works with px or anything that will util/parse-int"
  [math-fn]
  (fn [a b]
    (px (math-fn (px-val a) (px-val b)))))

(def ^{:doc "subtract anything that can be cast as px" }
  px-sub (create-px-math -))
(def ^{:doc "add tract anything that can be cast as px" }
  px-add (create-px-math +))
(def ^{:doc "multiply anything that can be cast as px" }
  px-mul (create-px-math *))
(def ^{:doc "divide anything that can be cast as px" }
  px-div (create-px-math /))
(def ^{:doc "mod anything that can be cast as px" }
  px-mod (create-px-math mod))

(defn- unit->string [data]
  (if (keyword? data) (util/keyword->string data)
    (if-not (units/unit? data) data
      (let [unit (s/replace (str (:unit data)) ":" "")
            magnitude (:magnitude data) ]
        (str magnitude unit)))))

(defn calc
  "works with garden/units keywords and strings

  (calc :50vh :- (utits/px 24) :+ \"2em\")
      => \"calc(50vh - 24px + 2em)\""
  ([]
   (throw "calc args canot be empty"))
  ([& args]
   (str "calc(" (s/join " " (map unit->string args)) ")")))

(defn css-class
  "return css class string for truthy

  (css-class {:hidden false :selected true :error true})
  \"selected error\"

  (css-class \"app-container\" {:theme-dark true :theme-light false})
  \"app-container theme-dark\"
  "
  ([data] (css-class "" data))
  ([original-class-name data]
    (->> data
        (filter #(second %))
        (map #(util/keyword->string (first %)))
        ((util/partial-right conj original-class-name))
        (s/join " ")
        (s/trim))))


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
    {:background (darken primary-bg 3)}]
   [:&:hover
    {:background (darken primary-bg 5)} ]
   [:&:active
    {:background (darken primary-bg 7)} ]
   [:&:disabled
    {:background  (lighten primary-bg 5)
     :color disabled-fg
     :cursor :auto}]
   [:&.selected
    {:background selected-bg
     :color selected-fg}
    [:&:focus
     {:background (darken selected-bg 5)}]
    [:&:hover
     {:background (darken selected-bg 7)} ]
    [:&:active
     {:background (darken selected-bg 10)} ]
    [:&:disabled
     {:background  (lighten selected-bg 5)
      :color disabled-fg
      :cursor :auto
      }]
    ]])
  )
