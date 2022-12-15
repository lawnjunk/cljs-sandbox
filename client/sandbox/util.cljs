(ns sandbox.util)

(defn wait [ms f]
  (js/setTimeout f ms))

(def xxl js/console.log)
(def xxp println)
(defn vconcat
  [& args]
  (into [] (apply concat args)))

(defn location-get []
  (let [hash js/window.location.hash]
    (if hash hash "")))
