(ns supervisor.unit.header-request-pending-icon
  (:require
    [supervisor.util :as util]
    [reagent.core :as reagent]
    [spade.core :as spade]
    [supervisor.data.theme :as d-theme]
    [supervisor.data.request-metrix :as d-metrix]
    [supervisor.style :as style])
  )

; lololol switch skew direction after each batch of requests
; very unnecsessary but dev v nice touch
(def is-skew-pos (reagent/atom false))

(spade/defkeyframes anime-header-request-pending-icon [color-1 color-2]
    [:0% {:background color-1
          :border-radius :55%}]
    [:100% {:background color-2
            :border-radius :10%
            :transform (if @is-skew-pos "skew(-12deg)" "skew(12deg)")}])

(spade/defclass css-header-request-pending-icon
  [theme is-pending]
  (let [{:keys [pallet]} theme
        header-height (get-in theme [:size :header-height])
        height (style/px-sub header-height 20)
        width (style/px-add height 20)
        pad (style/px-div height 2)
        anime-color-1 (:pending-1 pallet)
        anime-color-2 (:pending-2 pallet)
        ]
    [:&
     {:display :inline-block
     :width (style/px-add width (style/px-mul pad 2))
     :height :100%
     :float :left}
    [:.pending-icon
     {:display :block
      :position :relative
      :background (:pending-2 pallet)
      :animation-name (if is-pending (anime-header-request-pending-icon anime-color-1 anime-color-2) :none)
      :animation-direction :alternate
      :animation-iteration-count :infinite
      :animation-duration :200ms
      :border-radius :2%
      :height height
      :width width
      :left pad
      :top pad
      :text-align :center
      :font-size :.9em
      :color (style/lighten 30 (:pending-2 pallet))
      }
     [:&:hover {:background (style/darken 5 (:pending-2 pallet))}]
     [:&:active {:background :orange}]
     ]]))

; TODO capture a list of http erros and open in them in a debug modal
; when unit-header-request-pending-icon is clicked?
; or mabey just pretty print db on click

(defn unit [props]
  (let [theme @(d-theme/fetch)
        request-metrix @(d-metrix/fetch)
        pending (:pending request-metrix)
        is-pending (not= 0 pending)
        pending-content (if is-pending pending "")]
    (when-not is-pending (reset! is-skew-pos (not @is-skew-pos)))
    [:div (style/merge-props props {:class (css-header-request-pending-icon theme is-pending)})
      [:button.pending-icon
       {:on-click #(println "request-metrix" request-metrix)} pending-content]]))
