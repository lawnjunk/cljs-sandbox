(ns sandbox.util
  (:require 
    ["uuid" :as uuidlib]
    [reagent.core :as reagent]
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

(defn keywordify
  "if data is collection apply clojure.walk/keywordize-keys"
  [data]
  (if-not (coll? data)
    data
    (keywordize-keys data)))

(defn get-order [a b] 
  (if (< a b) :less 
    (if (> a b) :greater 
      :equal)))


(defn clamp-min [value vmin]
  (if (< value vmin) vmin value))

(defn clamp-max [value vmax]
  (if (> value vmax) vmax value))

(defn clamp [value vmin vmax]
  (-> value
      (clamp-min vmin)
      (clamp-max vmax)))
