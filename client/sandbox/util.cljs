(ns sandbox.util
  (:require 
    ["uuid" :as uuidlib]
    [clojure.walk :refer [keywordize-keys]]))

(def id uuidlib/v4)


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
  [data]
  (if (nil? data)
    nil
    (keywordize-keys data)))
