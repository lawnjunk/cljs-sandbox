(ns supervisor.unit.click-copy-span
  (:require
    [supervisor.util :as util]
    [spade.core :as spade]
    [supervisor.data.theme :as d-theme]
    [supervisor.style :as style]
    ))

; TODO add class for wiglees
(spade/defclass css-click-copy-span
  [height]
  (let [pallet @(d-theme/fetch-pallet)
        bg-color (or :#a0afbf (:button-debug pallet))]
    [:&
     {
      :height height
      :line-height height
      :display :inline-block
      :outline [[:none :!important]]
      :text-decoration :underline
      :font-weight :bold
      }
     [:&:hover
      {
      :height height
      :line-height height
      :color (style/darken 25 bg-color)
      :font-size :.95em
      }]
     [:&::before
      {:color :black
       :content (util/wrap-quotes "シ")
       :font-size :1.25em
      :font-weight :bold
       }]
      [:&:hover::before
       {:color :black
        :font-size :1em
      :font-weight :bold
        :content (util/wrap-quotes "copy(")}]
      [:&:hover::after
       {:color :black
       :font-size :1.25em
      :font-weight :bold
        :content (util/wrap-quotes ")")}]
     ]))

(defn unit
  "click-copy-icon
  ** props **
  :height -> line-height
  :copy -> text to copy "
  [props]
  (let [{:keys [copy height]} props
        height (or height :1em)
        param (dissoc props :copy ) ]
    [:span
     (style/merge-props
       param
       {:on-click #(util/copy-to-clipboard copy)
       :class (css-click-copy-span height)
       })
     copy
     ]))
