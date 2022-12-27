(ns sandbox.util
  (:require 
     ["uuid" :as uuidlib]
     [reagent.core :as reagent]
     [garden.units :as units]
     [clojure.string :as s]
     [clojure.walk :refer [keywordize-keys]] 
     [oops.core :as oops]
     [goog.functions :as goof])
  (:import goog.Uri.QueryData))

(defn- xxd-create [logger]
  (fn dbg
    ([stuff]
      (logger "DBG: " stuff)
      stuff)
    ([title stuff]
      (logger "DBG" title ">>" stuff)
      stuff)))

(def xxl js/console.log)
(def xxp println)
(def xxdp (xxd-create xxp))
(def xxdl (xxd-create xxl))

(defn partial-right
  [f & partial-args]
  (fn [& args]
    (apply f (concat args partial-args))))

(defn debounce
  [ms f]
  (goof/debounce f ms))

(defn throttle
  [ms f]
  (goof/throttle f ms))

(defn once
  [f]
  (goof/once f))

(defn wait
  "wate ms delay then run f
   returns a cancel fn

   (wait 200 handler)

   (let [cancel (wait 200 handler)]
     ...
     (cancel))"
  [ms f ]
  (let [timeout-id (js/setTimeout f ms)]
    (println "wait-start" timeout-id)
    (fn []
      (println "wait-cancel" timeout-id)
      (js/clearTimeout timeout-id))))

(def id-gen uuidlib/v4)
(defn id-atom [] (reagent/atom (id-gen)))

(defn vconcat
  [& args]
  (into [] (apply concat args)))


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
        (map #(keyword->string (first %)))
        ((partial-right conj original-class-name))
        (s/join " ")
        (s/trim))))

(defn px-val
  [val]
  (if (number? val) val (:magnitude val)))

(defn px-div
  [a b]
  (units/px (/ (px-val a) (px-val b))))
(def px-sub units/px-)
(def px-sum units/px+)
(def px-mul units/px+)

