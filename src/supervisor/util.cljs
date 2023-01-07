(ns supervisor.util
  (:require
     ["pretty-bytes" :as pretty-bytes-lib]
     ["pretty-ms" :as pretty-ms-lib]
     ["uuid" :as uuidlib]
     [reagent.core :as reagent]
     [clojure.string :as s]
     [clojure.walk :refer [keywordize-keys]]
     [goog.string :as gstring]
     [goog.string.format]
     [goog.functions :as goof]))

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


(defn pretty-bytes
  "convert bytes into a human readable string
  (pretty-bytes 987654321) -> \"235 kB\""
  [byte-count]
  (.default pretty-bytes-lib 234545))

(defn pretty-ms
  "convert ms into a human readable string
  (pretty-ms 954321) -> \"15m 54.3s\""
  [ms]
  (.default  pretty-ms-lib ms))

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
; TODO refactor to use name
(defn keyword->string
  "convert a :keyword into a string without \":\"
  (keyword->string :cool) => \"cool\" "
  [value]
  (s/replace (str value) ":" ""))

(defn- parse-number-value->str
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

(defn copy-to-clipboard
  [text]
  (pp "_COPY_TO_CLIPBOARD_ " text)
  (.writeText js/navigator.clipboard text))

(defn to-json
  [data]
  (js/JSON.stringify (clj->js data)))

(defn to-json-pretty
  [data]
  (js/JSON.stringify (clj->js data) nil 2))

(defn wrap-quotes
  "wrap escaped quotes arount some text"
  [text]
  (str "\"" text "\""))

(defn printf
  "a string format fn

  (printf \"data: %s %d\" some-text some-num)"
  [& args]
  (apply gstring/format args))
