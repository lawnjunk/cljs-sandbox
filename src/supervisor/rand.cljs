(ns supervisor.rand
   (:require
    [clojure.string :as s]
    [oops.core :as oops]
    ["@faker-js/faker" :refer [faker]]))

(defn ^:export bool
  []
  (< 0.5 (rand)))

(defn ^:export integer
  ([] (integer 0 100))
  ([amin amax]
    (let [diff (abs (- amax amin))]
      (+ (rand-int diff) amin))))

(defn ^:export word
  ([] (word 1))
  ([amount]
    ((oops/oget faker "random.words") amount)))

(defn ^:export line
  ([] (line 1))
  ([amount]
    ((oops/oget faker "lorem.lines") amount)))

(defn ^:export emoji
  []
  ((oops/oget faker "internet.emoji")))

(defn ^:export adjective
  []
  ((oops/oget faker "word.adjective")))

(defn ^:export noun
  []
  ((oops/oget faker "word.noun")))

(defn ^:export verb
  []
  ((oops/oget faker "word.verb")))

(defn ^:export hex
  ([] (hex 1))
  ([amount]
    ((oops/oget faker "datatype.hexadecimal") (clj->js {:length amount
                                                        :case :upper}))))
(defn ^:export interjection
  []
  ((oops/oget faker "word.interjection")))
                                                        :case :upper
(defn ^:export name-first
  []
  ((oops/oget faker "name.firstName")))

(defn ^:export name-last
  []
  ((oops/oget faker "name.lastName")))

(defn ^:export name-full
  []
  ((oops/oget faker "name.fullName")))

(defn ^:export job-title
  []
  ((oops/oget faker "name.jobTitle")))

(defn ^:export password
  []
  ((oops/oget faker "internet.password")))

(defn ^:export image-url
  []
  ((oops/oget faker "image.abstract") 500 500 true))

(defn ^:export email
  []
  (s/lower-case (js->clj ((oops/oget faker "internet.email")))))

(defn ^:export phone-number
  []
  ((oops/oget faker "phone.phoneNumber" "###-###-###")))

(defn ^:export phone-number-us
  []
  ((oops/oget faker "phone.phoneNumber") "+1 ###-###-###"))

(defn ^:export phone-number-tw
  []
  ((oops/oget faker "phone.phoneNumber") "+886 9##-###-###"))

(defn ^:export ip
  []
  ((oops/oget faker "internet.ip")))

(defn ^:export mac
  []
  ((oops/oget faker "internet.mac")))

(defn ^:export color-rgb
  []
  ((oops/oget faker "color.rgb") (clj->js {:prefix "#" :casing :upper})))

(defn ^:export color-hsl
  []
  ((oops/oget faker "color.hsl") (clj->js {:format :css})))

(defn ^:export city
  []
  ((oops/oget faker "address.city")))

(defn ^:export state
  []
  ((oops/oget faker "address.state")))

(defn ^:export country
  []
  ((oops/oget faker "address.country")))

(defn ^:export street
  []
  ((oops/oget faker "address.street")))

(defn ^:export address-line-1
  []
  ((oops/oget faker "address.streetAddress")))

(defn ^:export address-line-2
  []
  ((oops/oget faker "address.secondaryAddress")))

(defn ^:export zipcode
  []
  ((oops/oget faker "address.zipCode")))

(defn ^:export date-soon
  "days? is the range of days in the future (default 1)"
  ([] (date-soon 1))
  ([days]
    ((oops/oget faker "date.soon") days)))

(defn ^:export date-past
  "days? is the range of days in the past (default 1)"
  ([] (date-past 1))
  ([days]
    ((oops/oget faker "date.recent") days)))

(defn ^:export date-between
  "days? is the range of days in the past (default 1)"
  [start-date end-date]
  ((oops/oget faker "date.between") start-date end-date))
