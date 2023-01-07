(ns supervisor.unit.click-copy-icon
  (:require
    [supervisor.util :as util]
    [spade.core :as spade]
    [supervisor.data.theme :as d-theme]
    [supervisor.style :as style]
    ))

; TODO add class for wiglees
(spade/defclass css-click-copy-icon
  [height width]
  (let [pallet @(d-theme/fetch-pallet)
        bg-color (or :#a0afbf (:button-debug pallet))]
    [:&
     {:background :black
      :height height
      :width width
      :outline [[:none :!important]]
      :border-left [[(style/px-div width 8) :solid (style/lighten 3 bg-color )]]
      }
     [:&::before
      {:color :white
       :content (util/wrap-quotes "シ")
       :font-size :1.25em
       }]
      [:&:hover::before
       {:font-size :.8em
        :content (util/wrap-quotes "(co")}]
     [:&::after
      {:color :black
       :content (util/wrap-quotes "シ")
       :font-size :1.25em
       }]
      [:&:hover::after
       {:font-size :.8em
        :content (util/wrap-quotes "py)")}]
      (style/mixin-button-color bg-color)
     ]))

(defn unit
  "click-copy-icon

  ** props **
  :copy -> text to copy
  :height
  :width"
  [props]
  (let [{:keys [copy height width]} props
        param (dissoc props :copy :width :height) ]
    [:button
     (style/merge-props
       param
       {:on-click #(util/copy-to-clipboard copy)
       :class (css-click-copy-icon height width)})
     ]))
