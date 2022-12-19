(ns sandbox.page.storybook
  (:require
    [spade.core :as spade]
    [reagent.core :as reagent]
    [sandbox.unit.counter :refer [unit-counter]]
    [sandbox.unit.spinner-list :refer [unit-spinner-list]]
    [sandbox.base :as <>]))

(spade/defclass css-storybook []
  {:background "red"
   :width "100%"
   :height "100%"
   }
  [:.left-panel 
   {:background "blue"
    :width "10%"
    :height "100vh"
    :float :left}
   [:h2 
    {:color "yellow"
     :text-align "center"}]
   [:.story-nav
    {:display :flex
     :flex-direction :column
     :justify-content :vertical-align}]
    [:a 
     {:width "100%"}]
   ]
  [:.content 
   {:background "orange"
    :width "90%"
    :float :left}])

(def uri-page-storybook "/#/storybook")

(defn unit-storybook-nav-item [item-name selected] 
  (let [href (str uri-page-storybook "/" item-name)
        ] 
    [:a {:href href
         :alt (str "show story: " item-name)
         :class ["seleceted"]
         } item-name ]))

; TODO store.params.pagename ur somthing auto mod querystring
; * param-set [page-name param value]
; * param-get [page-name] -> map of pages params
; * perhaps control with [Select {:option value}]

; TODO siik storybook mods
; * toggle :main.content size (small, med, large)
; * toggle :nav side or bottom
; * toggle :theme-dark :theme-light

(defn story-404 []
  [:div [:h1 "select a story"]])

(defn page-storybook [story]
  (let [story (if story (keyword story))]
    [:div {:class (css-storybook)}
     [:section.left-panel
       [:h2 ":story:"]
      [:nav.story-nav
       [unit-storybook-nav-item "counter" (= story "counter")]
       [unit-storybook-nav-item "spinner" (= story "spinner")]
       ]]
     [:main.content
      (case story
        :counter [unit-counter]
        :spinner [unit-spinner-list]
        [story-404])
      ]]))
