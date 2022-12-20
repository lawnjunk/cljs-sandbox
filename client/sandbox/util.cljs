(ns sandbox.util
  (:require 
    ["uuid" :as uuidlib]
    [reagent.core :as reagent]
    [garden.units :as units]
    [clojure.string :as s]
    [clojure.walk :refer [keywordize-keys]]))

(def id-gen uuidlib/v4)
(defn id-atom [] (reagent/atom (id-gen)))

(defn wait [ms f]
  (js/setTimeout f ms))

(def xxl js/console.log)
(def xxp println)
(defn- dbg-create [logger]
  (fn dbg
    ([stuff]
      (logger "DBG: " stuff)
      stuff)
    ([title stuff]
      (logger "DBG" title ">>" stuff)
      stuff)))

(def xxdp (dbg-create xxp))
(def xxdl (dbg-create xxl))

(defn vconcat
  [& args]
  (into [] (apply concat args)))

(defn location-get []
  (let [hash js/window.location.hash]
    (if hash hash "")))

(defn clamp-min [value vmin]
  (if (< value vmin) vmin value))

(defn clamp-max [value vmax]
  (if (> value vmax) vmax value))

(defn clamp [value vmin vmax]
  (-> value
      (clamp-min vmin)
      (clamp-max vmax)))

(defn keywordify
  "if data is collection apply clojure.walk/keywordize-keys"
  [data]
  (if-not (coll? data)
    data
    (keywordize-keys data)))

(defn keyword->string
  "convert a :keyword into a string without \":\"

  (keyword->string :cool) => \"cool\" "
  [value]
  (s/replace (str value) ":" ""))

(defn- unit->string [data]
  (if (keyword? data) (keyword->string data)
    (if-not (units/unit? data) data
      (let [unit (s/replace (str (:unit data)) ":" "")
            magnitude (:magnitude data) ]
        (str magnitude unit)))))

(defn css-calc
  "works with garden/units keywords and strings

  (calc :50vh :- (utits/px 24) :+ \"2em\")
      => \"calc(50vh - 24px + 2em)\""
  ([]
   (throw "calc args canot be empty"))
  ([& args]
   (str "calc(" (s/join " " (map unit->string args)) ")"))) 

(defn px-val 
  [val]
  (if (number? val) val (:magnitude val)))

(defn px-div
  [a b]
  (units/px (/ (px-val a) (px-val b))))

(def px-sub units/px-)
(def px-sum units/px+)
(def px-mul units/px+)
