(ns sandbox.page.storybook
  (:require
    [spade.core :as spade]
    [sandbox.unit.counter :refer [unit-counter]]
    [sandbox.unit.spinner-list :refer [unit-spinner-list]]
    [sandbox.style :refer [pallet]]
    [sandbox.base :as <>])
  )

(spade/defclass css-storybook-left []
  {:background "red"
   :width "100%"
   :height "100%"
   }
  [:.panel
   {:background (:storybook-panel @pallet) :float :left}
   [:h2
    {:color (:fg @pallet)
     :text-align "center"}]
   [:.story-nav
    {:display :flex
     :flex-direction :column
     :justify-content :vertical-align}]
    [:a
     {:width "100%"}]
   ]
  [:.content
   {
    :background "blue"
    :width "90%"
    :height "100vw"
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

(defn page-storybook [query story]
  (let [story (if story (keyword story))]
    [:div {:class (css-storybook-left)}
     [:section.panel
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
