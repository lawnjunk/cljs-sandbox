(ns supervisor.space
  (:require
    [reagent.core :as reagent]
    [supervisor.util :as util]
    [supervisor.fake :as fake]
    [supervisor.style :as style]
    [supervisor.environ :as environ]
    [garden.stylesheet :refer [at-media]]
    [spade.core :as spade]))

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

(spade/defclass css-space-v
  [size]
  (mixin-box-default
    {:height size}))

(spade/defclass css-space-vf
  [size]
  (mixin-box-default
    {:height size
     :float :left}))

(spade/defclass css-space-h
  [size]
  [:&
    (at-media
      {:max-width :777px}
      {:width :100%
       :height :auto})
    (mixin-box-default
      {:width size})])

(spade/defclass css-space-hf
  [size]
  [:&
    (at-media
      {:max-width :777px}
      {:width :100%
       :height :auto})
    (mixin-box-default
      {:width size
       :float :left})])

(spade/defclass css-space-flex-start
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


; getting props and children of current componet
; https://cljdoc.org/d/reagent/reagent/1.1.1/doc/tutorials/interop-with-react?q=children#getting-props-and-children-of-current-component

(defn make-normal-space
  [css-fn]
  (fn [props & children]
    (let [size (if (map? props) (:size props) props)
          props (when (map? props) (dissoc props :size))
          debug-props (when environ/DEBUG_BOXES {:style {:background (fake/color-rgb)}})
          css-props (merge debug-props {:class (css-fn size)})
          props (style/merge-props props css-props)]
      (util/vconcat [:div props] children))))


(defn make-flex-space
  [css-fn]
  (fn []
    (let [this (reagent/current-component)
          props (reagent/props this)
          children (reagent/children this)
          debug-props (when environ/DEBUG_BOXES {:style {:background (fake/color-rgb)}})
          css-props (merge debug-props {:class (css-fn)})
          props (style/merge-props props css-props)]
      (into [:div props] children))))

(def ^{:doc "normal row"}
  n-row (make-normal-space css-space-v))
(def ^{:doc "floated left row"}
  f-row (make-normal-space css-space-vf))
(def ^{:doc "normal column"}
  n-col (make-normal-space css-space-h))
(def ^{:doc "floated left column"}
  f-col (make-normal-space css-space-hf))

(defn clearfix
  "a css clearfix to stop floating"
  [] [:div.clearfix])

(def ^{:doc "flex box (horizontal) justify-content: start"}
  x-start (make-flex-space (partial css-space-flex-start :start false)))
(def ^{:doc "flex box (horizontal) justify-content: end"}
  x-end (make-flex-space (partial css-space-flex-start :end false)))
(def ^{:doc "flex box (horizontal) justify-content: space-evenly"}
  x-even (make-flex-space (partial css-space-flex-start :space-evenly false)))
(def ^{:doc "flex box (horizontal) justify-content: center"}
  x-center (make-flex-space (partial css-space-flex-start :center false)))

(def^{:doc "flex box (verticle) justify-content: center"}
  y-start (make-flex-space (partial css-space-flex-start :start true)))
(def ^{:doc "flex box (verticle) justify-content: center"}
  y-end (make-flex-space (partial css-space-flex-start :end true)))
(def ^{:doc "flex box (verticle) justify-content: center"}
  y-even (make-flex-space (partial css-space-flex-start :space-evenly true)))
(def ^{:doc "flex box (verticle) justify-content: center"}
  y-center (make-flex-space (partial css-space-flex-start :center true)))

(def box (make-flex-space css-div))

