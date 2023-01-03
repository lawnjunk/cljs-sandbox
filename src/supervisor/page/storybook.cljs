(ns supervisor.page.storybook
  (:require
    [spade.core :as spade]
    [supervisor.unit.counter :refer [unit-counter unit-counter-doc]]
    [supervisor.unit.spinner-list :refer [unit-spinner-list]]
    [supervisor.story.story-form-login :refer [story-unit-form-login]]
    [supervisor.story.story-click-copy-icon :refer [story-click-copy-icon]]
    [supervisor.data.theme :as theme]
    [supervisor.util :as util]
    [supervisor.style :as style]
    [supervisor.base :as <>]))

(def story-route-map
  {:counter [:div [unit-counter] [unit-counter-doc]]
   :form-login [story-unit-form-login]
   :click-copy-icon [story-click-copy-icon]
  })

(spade/defclass css-storybook-left [theme]
  (let [pallet (:pallet theme)
        main-height (get-in theme [:size :main-height])]
    [:&
     {:background :red
      :width :100%
      :height :100%
      }
     [:.panel
      {:background (:storybook-panel pallet)
       :float :left
       :height :100%
       :width :15%}
      [:h2
       {:display :none
        :color (:fg pallet)
        :text-align "center"}]
      [:.story-nav
       {:display :flex
        :flex-direction :column
        :justify-content :vertical-align}]
      [:.nav-item-container
       {
        :width :100%
        :height :100%}]
       [:.nav-item
        {:display :block :width :100%
         :color :black
         :font-size :1.25em
         :padding [[ :2px :10px]]
         :margin-bottom :5px
         :background (:storybook-nav-unselected pallet)}
        [:&:focus
         {:background (style/lighten (:storybook-nav-unselected pallet) 5)}]
        [:&:hover
         {:background (style/lighten (:storybook-nav-unselected pallet) 10)}]
        [:&:active
         {:background (style/lighten (:storybook-nav-unselected pallet) 15)}]
        [:&.selected
         {:background (:storybook-nav-selected pallet) }
          [:&:focus
           {:background (style/lighten (:storybook-nav-selected pallet) 5)}]
          [:&:hover
           {:background (style/lighten (:storybook-nav-selected pallet) 10)}]
          [:&:active
           {:background (style/lighten (:storybook-nav-selected pallet) 15)}]
         ]]]
     [:.content
      {:background (:bg pallet)
       :padding :2em
       :width :85%
       :height main-height
       :float :left}]]))


(def uri-page-storybook "/storybook")

(defn unit-storybook-nav-item [item-key current-story]
  (let [item-name (name item-key)
        href (str uri-page-storybook "/"  item-name)
        selected (= current-story item-key)
        ]
    [<>/Hpush
     {:href href
      :alt (str "show story: " item-name)
      :class (style/css-class "nav-item" {:selected selected})}
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

(defn page
  "storybook page"
  [param]
  (let [css-theme @(theme/fetch)
        story-name (keyword (get param :id :none))
        story-view (get story-route-map story-name story-404)]
    [:div {:class (css-storybook-left css-theme)}
     [:section.panel
       [:h2 ":story:"]
      [:nav.story-nav
       (map (fn [item-key]
              [:div.nav-item-container {:key item-key}
              [unit-storybook-nav-item item-key story-name]])
            (keys story-route-map))
       ]]
     [:main.content story-view ]]))
