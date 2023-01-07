(ns supervisor.unit.blue-dot
  (:require
    [reagent.core :as reagent]
    [spade.core :as spade]
    [supervisor.base :as b]
    [supervisor.space :as s]
    [supervisor.util :as util]
    [supervisor.style :as style]
    [supervisor.data.theme :as d-theme]
    ))

(spade/defclass css-blue-dot []
  (let [pallet @(d-theme/fetch-pallet)
        dot-color (:blue-dot pallet)
        blue-dot-code-bg (:blue-dot-code-bg pallet)
        blue-dot-modal-bg (:blue-dot-modal-bg pallet)
        ]
    [:&
     {:display :inline-block}
     [:.the-dot
      {:width :15px
       :height :15px
       :min-height :15px
       :border-radius :50%
       :background dot-color}
      (style/mixin-button-color dot-color)
      ]

     [:.the-modal
      {:position :fixed
       :width :100vw
       :height :100vh
       :top :50vh
       :left :50vw
       :background blue-dot-modal-bg
       :padding :20px
       :transform [[ "translateY(-50%) translateX(-50%)"]]
       }
      [:button
       {:margin-right :10px}]
      ]
     [:.the-hud
      {:width :100%
       :max-width :700px
       :margin  [[ :0 :auto]] }
       [:button
        (style/mixin-button-color blue-dot-code-bg)
        ]
      ]
     [:.the-content
      {:background blue-dot-code-bg
       :white-space :pre
       :padding :20px
       :height :95%
       :max-width :700px
       :margin  [[ :0 :auto]]
       :overflow :scroll
       }]
     [:code
      {:background blue-dot-code-bg


       }]
     ]))

(defn part-code-highlight
  [language content]
  (let [hljs-class-name (str "language-" (name language))]
    (reagent/create-class
      {
      :component-did-mount #(.highlightAll js/hljs)
      :render
      (fn []
        [:code (style/tag hljs-class-name) content])})))

(defn unit
  "blue-dot
  this component is for debugging the data inside of a view
  click it to open a modal that exposes the json/edn that the view contins"
  [props]
  (let [is-open (reagent/atom false)
        data (get props :data)
        content (util/to-json-pretty data)
        ]
    (fn []
      [s/box (style/tag [:blue-dot (css-blue-dot)])
       [s/box
        {:class :the-dot
         :on-click #(swap! is-open not)}]
       (when @is-open
         [s/box (style/tag :the-modal)
          [s/box (style/tag :the-hud)
            [b/ButtonDebug {:on-click #(swap! is-open not) } "close"]
            [b/ButtonDebug {:on-click #(util/copy-to-clipboard content)} "copy"]]
          [:pre (style/tag :the-content)
           [part-code-highlight :json content]]
          ])
       ])))
