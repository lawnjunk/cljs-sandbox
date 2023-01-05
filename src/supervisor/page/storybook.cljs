(ns supervisor.page.storybook
  (:require
    [spade.core :as spade]
    [supervisor.book.story-form-login :refer [story-unit-form-login]]
    [supervisor.book.story-click-copy-icon :refer [story-unit-click-copy]]
    [supervisor.book.story-magic-counter :refer [story-unit-magic-counter]]
    [supervisor.book.story-space-layout :as space-layout]
    [supervisor.book.story-blue-dot :refer [story-blue-dot]]
    [supervisor.book.story-pomelo-metadata-banner :refer [story-pomelo-metadata-banner]]
    [supervisor.data.theme :as d-theme]
    [supervisor.util :as util]
    [supervisor.fake :as fake]
    [supervisor.style :as style]
    [supervisor.space :as s]
    [supervisor.base :as b]))

; NOTE: to add storys to side pannel just add keys to this map
; values must be vaild reagent/hiccup
(def story-route-map
  {:magic-counter [story-unit-magic-counter]
   :space-layout [space-layout/story]
   :click-copy [story-unit-click-copy]
   :blue-dot [story-blue-dot]
   :form-login [story-unit-form-login]
   :metadata-banner [story-pomelo-metadata-banner]
  })

(spade/defclass css-storybook-page []
  (let [theme @(d-theme/fetch)
        pallet (:pallet theme)
        main-height (get-in theme [:size :main-height])
        panel-color (:storybook-panel pallet)]
    [:&
     {:width :100vw
      :height main-height
      :background :red
      }
     [:.storybook-panel
      {:background panel-color}
      [:h2
       {:text-align :center
        :margin :10px
        }]
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
      :background :red
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
        selected (= current-story item-key)
        ]
    [b/Hpush
     {:key (str "link-to-" href)
      :href href
      :alt (str "show story: " item-name)
      :class (style/css-class (css-storybook-nav-item pallet) {:selected selected})}
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

(defn page
  "storybook page"
  [route]
  (let [pallet @(d-theme/fetch-pallet)
        current-story-name (keyword (get route :id :none))
        current-story-view (get story-route-map current-story-name [story-404])]
    [s/box {:class (css-storybook-page)}
     [s/f-col {:class "storybook-panel" :size :15%}
       [:h2 ":story:"]
       [s/box
        (map (partial part-item-storybook-nav current-story-name pallet)
             (keys story-route-map))]]
     [s/f-col {:class "storybook-main" :size :85%} current-story-view ]]))
