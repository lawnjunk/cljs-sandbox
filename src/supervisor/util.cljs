(ns supervisor.util
  (:require
     ["pretty-bytes" :as pretty-bytes-lib]
     ["pretty-ms" :as pretty-ms-lib]
     ["uuid" :as uuidlib]
     [reagent.core :as reagent]
     [garden.units :as units]
     [clojure.string :as s]
     [clojure.walk :refer [keywordize-keys]]
     [oops.core :as oops]
     [fipp.edn :as fipp]
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

(defn pp [& args]
  (apply js/console.log (clj->js args {:keyword-fn str})))


(def xxl js/console.log)
(def xxp pp)
(def xxdp (xxd-create pp))
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
  "wate ms wait then run f
   returns a cancel fn

   (wait 200 handler)

   (let [cancel (wait 200 handler)]
     ...
     (cancel))"
  [ms f ]
  (let [timeout-id (js/setTimeout f ms)]
    (comment println "wait-start" timeout-id)
    (fn []
      (comment println "wait-cancel" timeout-id)
      (js/clearTimeout timeout-id))))

(defn interval
  "wate ms wait then run f and repeat
   returns a cancel fn

   (wait 200 handler)

   (let [cancel (wait 200 handler)]
     ...
     (cancel))"
   [ms f]
  (let [interval-id (js/setInterval f ms)]
    (comment println "interval-start" interval-id)
    (fn []
      (comment println "interval-cancel" interval-id)
      (js/clearInterval interval-id))))

(def id-gen uuidlib/v4)
(defn id-atom [] (reagent/atom (id-gen)))

(defn vconcat
  [& args]
  (into [] (apply concat args)))


(defn clamp-min [vmin value]
  (if (< value vmin) vmin value))

(defn clamp-max [vmax value]
  (if (> value vmax) vmax value))

(defn clamp [vmin vmax value]
  (-> value
      (clamp-min vmin)
      (clamp-max vmax)))

(defn keywordify
  "if data is collection apply clojure.walk/keywordize-keys"
  [data]
  (if-not (coll? data)
    data
    (keywordize-keys data)))

; lol could have used (name :value
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

(defn parse-number-value->str
  [value]
  (cond
    (keyword? value) (name value)
    (number? value) value
    (string? value) value
  :else (throw "not a parseable number")))

(defn parse-int
  "parse a string number or keyword into an int"
  [value]
  (js/parseInt (parse-number-value->str value)))

(defn parse-number
  "parse a string number or keyword into an float"
  [value]
  (.valueOf (js/Number. (parse-number-value->str value))))

(defn- copy-to-clipboard
  [text]
  (pp "_COPY_TO_CLIPBOARD_ " text)
  (.writeText js/navigator.clipboard text))
