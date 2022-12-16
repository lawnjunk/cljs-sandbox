(ns sandbox.util)

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

(def xxdp xxp)
(def xxdl xxl)

(defn vconcat
  [& args]
  (into [] (apply concat args)))

(defn location-get []
  (let [hash js/window.location.hash]
    (if hash hash "")))
