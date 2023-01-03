(ns supervisor.unit.click-copy-icon
  (:require
    [spade.core :as spade]
    [garden.selectors :as sel]
    [garden.util :as gutil]
    [supervisor.data.theme :as d-theme]
    [supervisor.style :as style]
    [supervisor.util :as util]))

; TODO add class for wiglees
(spade/defclass css-click-copy-icon
  [theme height width]
  (let [pallet (:pallet theme)
        bg-color (or :#a0afbf (:button-debug pallet))]
    [:&
     {:background :black
      :height height
      :width width
      :outline [[:none :!important]]
      :border-left [[(style/px-div width 8) :solid (style/lighten bg-color 3)]]
      }
     [:&::before
      {:color :white
       :content (gutil/wrap-quotes "シ")
       :font-size :1.25em
       }]
      [:&:hover::before
       {:font-size :.8em
        :content (gutil/wrap-quotes "(co")}]
     [:&::after
      {:color :black
       :content (gutil/wrap-quotes "シ")
       :font-size :1.25em
       }]
      [:&:hover::after
       {:font-size :.8em
        :content (gutil/wrap-quotes "py)")}]
      (style/mixin-button-color bg-color)
     ]))

(defn unit
  "click-copy-icon

  ** params **
  :copy -> text to copy
  :height
  :width"
  [options]
  (let [theme @(d-theme/fetch)
        {:keys [copy height width]} options
        param (dissoc options :copy :width :height)]
    [:button
     (merge
       {:on-click #(util/copy-to-clipboard copy)
       :class (css-click-copy-icon theme height width)})
     ]))

