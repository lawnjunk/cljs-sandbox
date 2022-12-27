(ns sandbox.cheatsheet)

;control
; do allows you to put more than one form together
; the result is the last value
(do
  (println "cool")
  (println "beans")
  "my dude")

; if what you expect (also try if-not)
(if true "hello" "goodbye")
(if false "hello" "goodbye")

; if with do for then and else
(if false
  (do
    (println  "cool beans")
    "hello")
  (do
    (println "not cool beans") 
    "goodbye"))

;when is like if but has no else (try when-not 
;the body is an implisit do
(when true
  (println "cool")
  (println "beans")
  "my dude")

; let allows you to create variables
; variables can be overwritten
(let [prefix  "slugbyte"
      prefix (clojure.string/upper-case prefix)
      domain "slugbyte.com"]
  (str prefix "@" domain))

; math and logic
(+ 1 2 3 4)
(-  123 23)
(/ 100 10)
(mod 12  2) ; remainder

; testing
(number? 22) ;true
(number? 2.2) ;true
(int? 22.2) ; false
(int? 22)  true
(float? 22.2) ;true
(float? 22)  ;true
(string? "heell") ;true

; create a constant variable
(def some-value :cool-bean)
