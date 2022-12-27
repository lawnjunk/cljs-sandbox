; # about
; this module contains an api for working with the browser location
; all "replace" fns use the browser history api to replace the url
; ## useful fns
; * fetch-route - get the window.location {pathname}{query-string}
; * fetch-pathname - get window.location.pathname
; * fetch-query - get window.location query-string
; * fetch-map-from-query - convert the window.location query-string into a cljs map
; * replace-route! - replace window location using browser history api
; * replace-pathname! - replace window pathname using browser history api
; * replace-query! - replace window query-string using browser history api
; * replace-query-from-map - replace window query-string with an encoded cljs map using browser history api

(ns sandbox.location
  (:require
     [clojure.string :as s]
     [secretary.core :as secretary]
     [sandbox.util :as util])
  (:import goog.Uri.QueryData))

(defn fetch-route
  "{location.pathname}{location.query}

  e.g. /storybook/color-picker?color=7FA7FA"
  []
  (let [pathname (or (.-pathname js/window.location) "/")
        query (or (.-search js/window.location) "")]
    (str pathname query)))

(defn fetch-pathname
  "fetch the window location pathname"
  []
  (.-pathname js/window.location))

(defn fetch-query
  "fetch the window location query-string"
  []
  (let [search (.-search js/window.location)]
    (if (s/starts-with? search "?")
      (s/replace-first search "?" "")
      search)))

(defn replace-route!
  "using browser history api replace state with new route
   and secretary/dispatch! the new route"
  [^String route]
  (.replaceState js/window.history nil nil route)
  (secretary/dispatch! route))

(defn replace-query!
  "use the browser history api to replace the url query-string"
  [^String query-string]
  (let [pathname (fetch-pathname)
        search (if (s/starts-with? query-string "?") query-string (str "?" query-string))
        route (str pathname search)]
    (replace-route! route)))

(defn replace-pathname!
  "user the browser history api to replace the url pathname"
  [^String pathname]
  (let [search (str "?" (fetch-query))
        route (str pathname search) ]
    (replace-route! route)))

(defn- query-data-from-string
  "convert a query-string into a goog.Uri.QueryData"
  [^String query-string]
  (QueryData. (js/decodeURIComponent query-string)))

(defn- decode-uri-encoded-values
  "decode URI safe strings into normal text
   example: '%20' -> ' '
  "
  [value]
  (cond
    (vector? value) (into [] (map decode-uri-encoded-values value))
    (list? value) (map decode-uri-encoded-values value)
    (string? value) (js/decodeURIComponent value)
    :else value))

(defn- encode-values-for-uri
  "encode lists,vectors,strings,and keywords
   into strings that are safe for URIs
   example: ' ' -> '%20'
  "
  [value]
  (cond
    (keyword? value) (js/encodeURIComponent (str value)) 
    (vector? value) (into [] (map encode-values-for-uri value))
    (list? value) (map encode-values-for-uri value)
    (string? value) (js/encodeURIComponent value)
    :else value))

(defn- query-data-map-prep-for-uri
  "shallow encode the values of a map into URI safe strings"
  [data]
  (into {} (map (fn [[k v]] [k (encode-values-for-uri v)]) data)))


(defn- query-data-from-map
  "convert a 1 level deep map that contains only
  strings,numbers,keywords & vecors of those values
  into a goog.Uri.QueryData

  all the values stored int the QueryData will be URI encioded
  "
  [data]
  (QueryData.createFromMap
    (clj->js
      (query-data-map-prep-for-uri data))))

(defn- query-data-to-string
  "generate a query string from a goog.Uri.QueryData"
  [^QueryData qd]
  (.toDecodedString qd))

(defn- query-data-value-parse
  "from strings convert data into strings,keywords, and numbers

  if the string starts with ':' it will return a :keyword

  if the string can be parsed as a number without becoming NaN it will 
  return a number
  "
  [value]
  (let [is-keyword (s/starts-with? value ":")
        value-as-keyword (keyword (s/replace value ":" ""))
        value-as-number (.valueOf (js/Number. value))
        is-number (not (js/isNaN value-as-number))]
    (cond
      is-keyword value-as-keyword
      is-number value-as-number
      :else value)))

(defn- query-data-get-value
  "get a value for key from a QueryData
   if there are multiple entries for that value it will return a list

   values can be strings, numbers, or keywords
  string,keyword,number,and vectors of those are supported
  "
  [^QueryData qd k]
  (let [k (util/keyword->string k)]
    (when (.containsKey qd k)
      (let [values (js->clj (.getValues qd k))
            is-list (> (count values) 1) ]
      (if is-list
        (into [] (map query-data-value-parse values))
        (query-data-value-parse (.get qd k)))))))

(defn- query-data-to-map
  "convert a #js goog.Uri.QueryData into a clojure map
  all of the query data values will be URI decoded into their original form
  string,keyword,number, and vectors of those are supported
  "
  [^QueryData qd]
  (let [key-list (.getKeys qd)
        entries (map #(identity [% (query-data-get-value qd %)]) key-list)]
    (->> entries
         (into {})
         (decode-uri-encoded-values)
         (util/keywordify))))


(defn replace-query-from-map!
  "encode and write a map into the query string
  the map can only contain strings,keywords,numbers, and lists of those
  (WARNING: no nested maps)"
  [data]
  (-> data
      (query-data-from-map)
      (query-data-to-string)
      (replace-query!)))

(defn fetch-map-from-query
  "decode the window location's query-string into a clj map
  the map can only contain strings,keywords,numbers, and lists of those"
  []
  (-> (fetch-query)
      (query-data-from-string)
      (query-data-to-map)))
