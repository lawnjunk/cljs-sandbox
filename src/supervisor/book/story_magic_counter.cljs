(ns supervisor.book.story-magic-counter
  (:require
    [supervisor.util :as util]
    [spade.core :as spade]
    [supervisor.unit.magic-counter :as magic-counter]))

(spade/defclass css-unit-counter-doc []
  {:background "#fafafa"
   :padding :15px
   :margin-top :10px}
  [:ul
   {:margin-left :15px}
   [:ul
    {:margin-left :25px }]])

(defn part-counter-doc []
  [:div {:class (css-unit-counter-doc)}
   [:h1 "when to use db vs qdb vs atoms?"]
   [:ul
    [:li "app state goes in db"]
    [:ul
     [:li "the random number used to generate the color "]]
    [:li "user prefernces should stored in qdb"]
    [:ul
     [:li "if auto in is on"]
     [:li "interval in ms"]
     [:li "if a url is loaded with the query set it will be prefered over defaults"]]
    [:li "reagent atoms should be used to keep track of component state" ]
    [:ul
     [:li "a-delta-* are used to keep track of changes to state so that
      the old interval can be canceled and a new one can begin"]
     [:li "a-cancel-fn is used to keep track of the latest interval canel fn"]]
    ]])

(defn story-unit-magic-counter
  []
  [:div
   [magic-counter/unit]
   [part-counter-doc]])
