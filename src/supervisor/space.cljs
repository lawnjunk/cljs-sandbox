; helpful componets for working with space (layout)
(ns supervisor.space
  (:require
    [reagent.core :as reagent]
    [spade.core :as spade]
    [supervisor.util :as util]
    [supervisor.fake :as fake]
    [supervisor.style :as style :refer [at-media]]
    [supervisor.environ :as environ]
    ))

(defn mixin-box-default
  [options]
  (merge {:box-sizing :border-box
   :width :100%
   :height :100%
   :position :relative
   } options))

(spade/defclass css-div
  []
  [:&
    (at-media
      {:max-width :777px}
      {:height [[:auto "!important"]]})])

(spade/defclass css-space-floatable
  [is-row is-float size]
  (let [size-opt (if is-row
                   {:height size}
                   {:width size})
        float-opt (when is-float {:float :left})]
  [:&
    (at-media
      {:max-width :777px}
      {:width :100%
       :height :auto})
    (mixin-box-default
      (merge size-opt float-opt))]))

(spade/defclass css-space-flexable
  [justify-content is-column]
  (let [flex-direction (if is-column :column :row)]
  [:&
    (at-media
      {:max-width :777px}
      (merge
        {:position :static
         :width :100%
         :height :auto
         :display :flex
         :justify-content :center
         :box-sizing :border-box
         :flex-direction flex-direction
         }
        (when is-column {:height :100%})))
    (merge
      (mixin-box-default
        (merge  {:position :static
         :display :flex
         :height :auto
         :flex-direction flex-direction
         :justify-content justify-content }
        (when is-column { :height :100%}))
        ))]))

(defn- make-float-space
  "props is allways required

  if environ.DEBUG_BOXES is true, colors will be random

  can be a size or a normal props map with :size
  so the following are equal
  [s/f-row :42px ...children]
  [s/f-row {:size :42px ...} ...children]"
  [css-fn]
  (fn [props & children]
    (let [size (if (map? props) (:size props) props)
          props (when (map? props) (dissoc props :size))
          debug-props (when environ/DEBUG_BOXES {:style {:background (fake/color-rgb)}})
          css-props (merge debug-props {:class (css-fn size)})
          props (style/merge-props props css-props)]
      (util/vconcat [:div props] children))))


; rows and columns
(def ^{:doc "normal row"}
  n-row (make-float-space (partial css-space-floatable true false)))
(def ^{:doc "floated left row"}
  f-row (make-float-space (partial css-space-floatable true true)))
(def ^{:doc "normal column"}
  n-col (make-float-space (partial css-space-floatable false false)))
(def ^{:doc "floated left column"}
  f-col (make-float-space (partial css-space-floatable false true)))
(defn clearfix
  "a css clearfix to stop floating"
  [] [:div.clearfix])

; getting props and children of current componet
; https://cljdoc.org/d/reagent/reagent/1.1.1/doc/tutorials/interop-with-react?q=children#getting-props-and-children-of-current-component
(defn- make-space
  "react props are optional

  if environ.DEBUG_BOXES is true, colors will be random

  both are ok
  [s/box ...children]
  [s/box {} ...children]"
  [css-fn]
  (fn []
    (let [this (reagent/current-component)
          props (reagent/props this)
          children (reagent/children this)
          debug-props (when environ/DEBUG_BOXES {:style {:background (fake/color-rgb)}})
          css-props (merge debug-props {:class (css-fn)})
          props (style/merge-props props css-props)]
      (into [:div props] children))))

; row flexbox
(def ^{:doc "flex box (horizontal) justify-content: start"}
  x-start (make-space (partial css-space-flexable :start false)))
(def ^{:doc "flex box (horizontal) justify-content: end"}
  x-end (make-space (partial css-space-flexable :end false)))
(def ^{:doc "flex box (horizontal) justify-content: space-evenly"}
  x-even (make-space (partial css-space-flexable :space-evenly false)))
(def ^{:doc "flex box (horizontal) justify-content: center"}
  x-center (make-space (partial css-space-flexable :center false)))

; column flexbox
(def^{:doc "flex box (verticle) justify-content: center"}
  y-start (make-space (partial css-space-flexable :start true)))
(def ^{:doc "flex box (verticle) justify-content: center"}
  y-end (make-space (partial css-space-flexable :end true)))
(def ^{:doc "flex box (verticle) justify-content: center"}
  y-even (make-space (partial css-space-flexable :space-evenly true)))
(def ^{:doc "flex box (verticle) justify-content: center"}
  y-center (make-space (partial css-space-flexable :center true)))

; normal box
(def box (make-space css-div))

(defn hook
  [& _]
  (let [this (reagent/current-component)
        props (reagent/props this)
        children (reagent/children this)
        pre-mount (:preload props)
        pre-mount-conf (when pre-mount
                         {:component-will-mount pre-mount})
        post-mount (:postload props)
        post-mount-conf (when post-mount
                          {:component-did-mount post-mount})
        pre-unmount (:unload props)
        pre-unmount-conf (when post-mount
                          {:component-will-unmount pre-unmount})
        props (dissoc props :preload :postload :unload)
        debug-props (when environ/DEBUG_BOXES {:style {:background (fake/color-rgb)}})
        css-props (merge debug-props {:class (css-div)})
        props (style/merge-props props css-props)
        render-conf {:render (fn [] [into [:div props] children])}
        conf (merge render-conf pre-mount-conf post-mount-conf pre-unmount-conf)]
    (reagent/create-class conf)))
