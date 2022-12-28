(ns supervisor.page.storybook
  (:require
    [spade.core :as spade]
    [garden.color :as color]
    [supervisor.unit.counter :refer [unit-counter unit-counter-doc]]
    [supervisor.unit.spinner-list :refer [unit-spinner-list]]
    [supervisor.unit.form-login :refer [story-unit-form-login]]
    [supervisor.style :as style]
    [supervisor.style :refer [pallet]]
    [supervisor.util :as util]
    [supervisor.base :as <>])
  )

(spade/defclass css-storybook-left []
  {:background "red"
   :width "100%"
   :height "100%"
   }
  [:.panel
   {:background (:storybook-panel @pallet) 
    :float :left
    :height :100%
    :width :10%}
   [:h2
    {:display :none
     :color (:fg @pallet)
     :text-align "center"}]
   [:.story-nav
    {:display :flex
     :flex-direction :column
     :justify-content :vertical-align}]
    [:.nav-item
     {:width "100%"
      :color :black
      :font-size :1.25em
      :padding [[ :2px :10px]] 
      :margin-bottom :5px
      :background (:storybook-nav-unselected @pallet)}
     [:&:focus 
      {:background (color/lighten (:storybook-nav-unselected @pallet) 5)}]
     [:&:hover
      {:background (color/lighten (:storybook-nav-unselected @pallet) 10)}]
     [:&:active
      {:background (color/lighten (:storybook-nav-unselected @pallet) 15)}]
     [:&.selected
      {:background (:storybook-nav-selected @pallet) }
       [:&:focus 
        {:background (color/lighten (:storybook-nav-selected @pallet) 5)}]
       [:&:hover
        {:background (color/lighten (:storybook-nav-selected @pallet) 10)}]
       [:&:active
        {:background (color/lighten (:storybook-nav-selected @pallet) 15)}]
      ]]]
  [:.content
   {:background (:bg @pallet)
    :padding :2em
    :width "90%"
    :height (or style/size-px-main-height :100vw) 
    :float :left}])

(def uri-page-storybook "/storybook")

(defn unit-storybook-nav-item [item-name selected] 
  (let [href (str uri-page-storybook "/" item-name)] 
    [<>/Hpush
     {:href href
      :alt (str "show story: " item-name)
      :class (util/css-class "nav-item" {:selected selected})}
     "> " item-name ]))

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

(defn page-storybook [param]
  (let [story (keyword (get param :id :none))]
    [:div {:class (css-storybook-left)}
     [:section.panel
       [:h2 ":story:"]
      [:nav.story-nav
       [unit-storybook-nav-item "form-login" (= story :form-login)]
       [unit-storybook-nav-item "counter" (= story :counter)]
       [unit-storybook-nav-item "spinner" (= story :spinner)]
       ]]
     [:main.content
      (case story
        :counter [:div [unit-counter] [unit-counter-doc]]
        :spinner [unit-spinner-list]
        :form-login [story-unit-form-login]
        [story-404])
      ]]))
