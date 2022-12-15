(ns list-demo.util.util)


(def xxl js/console.log)
(def xxp println)


(defn do-thing []
  "not really a thing")

(defn vconcat 
  [& args]
  (into []  (apply concat args)))

(vconcat [123 1 654] [24 54 6] [6 6 4])


