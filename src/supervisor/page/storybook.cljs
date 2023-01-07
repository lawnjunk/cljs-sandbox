(ns supervisor.page.storybook
  (:require
    [spade.core :as spade]
    [supervisor.book.story-form-login :refer [story-unit-form-login]]
    [supervisor.book.story-click-copy-icon :refer [story-unit-click-copy]]
    [supervisor.book.story-magic-counter :refer [story-unit-magic-counter]]
    [supervisor.book.story-space-layout :as space-layout]
    [supervisor.book.story-blue-dot :refer [story-blue-dot]]
    [supervisor.book.story-pomelo-metadata-banner :refer [story-pomelo-metadata-banner]]
    [supervisor.book.story-search-bar :as search-bar]
    [supervisor.book.story-changelog :as changelog]
    [supervisor.book.story-filter-box :as filter-box]
    [supervisor.unit.search-bar :as unit-search-bar]
    [supervisor.data.theme :as d-theme]
    [supervisor.data.search-bar :as d-search-bar]
    [supervisor.util :as util]
    [supervisor.fake :as fake]
    [supervisor.style :as style]
    [supervisor.space :as s]
    [supervisor.base :as b]))

; NOTE: to add storys to side pannel just add keys to this map
; values must be vaild reagent/hiccup
(def story-route-map (into (sorted-map)
  {:magic-counter [story-unit-magic-counter]
   :space-layout [space-layout/story]
   :click-copy [story-unit-click-copy]
   :blue-dot [story-blue-dot]
   :search-bar [search-bar/story]
   :form-login [story-unit-form-login]
   :filter-box [filter-box/story]
   :metadata-banner [story-pomelo-metadata-banner]
   :changelog [changelog/story]
  }))

(spade/defclass css-storybook-page []
  (let [theme @(d-theme/fetch)
        pallet (:pallet theme)
        main-height (get-in theme [:size :main-height])
        panel-color (:storybook-panel pallet)]
    [:&
     {:width :100vw
      :height main-height
      }
     [:.storybook-panel
      {:background panel-color}
      [:h2
       {:text-align :center
        :margin :10px
        }]

      [:.search-bar
       [:input {:width :71% :float :left :margin :0}]
       [:button {:width (style/calc  :30% :- :10px) :float :right}]]
      ]
     [:.storybook-main
      {:background (:bg pallet)
       :padding :10px
       }]]))

(def uri-page-storybook "/storybook")
(spade/defclass css-storybook-nav-item [pallet]
  (let [main-color  (->> :storybook-panel
                        (get pallet)
                        (style/lighten 10))
        selected-color  (->> :header-bg
                             (get pallet)
                             (style/lighten 10))]
    [:&
     {:display :inline-block
      :width :100%
      :margin-bottom :5px
      :padding :5px
      :outline :none
      }
     (style/mixin-button-color main-color selected-color)
     ]))

(defn part-item-storybook-nav [current-story pallet item-key]
  (let [item-name (name item-key)
        href (str uri-page-storybook "/"  item-name)
        is-selected (= current-story item-key)
        ]
    [b/Hpush
     {:key (str "link-to-" href)
      :href href
      :alt (str "show story: " item-name)
      :class (style/tag-value
               [{:selected is-selected}
                (css-storybook-nav-item pallet)])}
     "> " item-name ]))

; TODO store.params.pagename ur somthing auto mod querystring
; * param-set [page-name param value]
; * param-get [page-name] -> map of pages params
; * perhaps control with [Select {:option value}]

; TODO siik storybook mods
; * toggle :main.content size (small, med, large)
; * toggle s/box side or bottom
; * toggle :theme-dark :theme-light
(defn story-404 []
  [s/box [:h1 ":)"]])

(defn- show-story?
  [search-value story-key]
  (util/fuzzy-match? (name story-key) search-value))

(defn page
  "storybook page"
  [route]
  (let [pallet @(d-theme/fetch-pallet)
        search-value @(d-search-bar/fetch-term :story)
        current-story-name (keyword (get route :id :none))
        current-story-view (get story-route-map current-story-name [story-404])
        story-key-list (keys story-route-map)
        story-key-list (filter (partial show-story? search-value) story-key-list)
        ]
    [s/box {:class (css-storybook-page)}
     [s/f-col {:class "storybook-panel" :size :15%}
       [:h2 ":story:"]
       [:p "search-value: " search-value]
       [unit-search-bar/unit {:search-term :story}]
       [s/box
        (map (partial part-item-storybook-nav current-story-name pallet)
             story-key-list)]]
     [s/f-col {:class "storybook-main" :size :85%} current-story-view ]]))
