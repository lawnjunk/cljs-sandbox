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
        dot-color (:blue-dot pallet)]
    [:&
     [:.the-dot
      {:width :20px
       :height :20px
       :min-height :20px
       :border-radius :50%
       :background dot-color}
      (style/mixin-button-color dot-color)
      ]

     [:.the-modal
      {:position :fixed
       :width :89vw
       :height :95vh
       :top :50vh
       :left :50vw
       :background :grey
       :padding :20px
       :transform [[ "translateY(-50%) translateX(-50%)"]]
       }
      [:button
       {:margin-right :10px}]
      ]

     [:.the-content
      {:background :#999999
       :white-space :pre
       :padding :20px
       :height :95%
       :overflow :scroll
       }]
     ]))


(defn unit
  "blue-dot

  this component is for debugging the data inside of a view
  click it to open a modal that exposes the json/edn that the view contins"
  [props]
  (let [is-open (reagent/atom false)
        data (get props :data)
        content (js/JSON.stringify (clj->js data) nil 2) ]
    (fn []
      [s/box {:class (css-blue-dot) }
       [s/box
        {:class "the-dot"
         :on-click #(swap! is-open not)}]

       (when @is-open
         [:div {:class "the-modal"}
          [b/ButtonDebug {:on-click #(swap! is-open not) } "close"]
          [b/ButtonDebug {:on-click #(util/copy-to-clipboard content)} "copy"]
          [:div {:class "the-content"}
            (str content)]
          ])
       ])))
