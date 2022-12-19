(ns sandbox.base
  (:require
    [spade.core :refer [defclass]]
    [sandbox.style :refer [color]]
    [sandbox.util :as util]))

(defn- el-create-no-css
  [tag-name ]
  (fn [options & children]
    (util/vconcat [tag-name options] children)))

(defn- el-create-with-css
  [tag-name style]
  (fn [options & children]
    (util/vconcat [tag-name (merge {:class (style)} options)] children)))

(defn- el-create-with-attribute
  [tag-name style attribute]
  (fn [options & children]
    (util/vconcat [tag-name (merge {:class (style)} attribute options)] children)))

(defn- el-create
  ([tag-name] (el-create-no-css tag-name))
  ([tag-name style] (el-create-with-css tag-name style))
  ([tag-name style attribute] (el-create-with-attribute tag-name style attribute)))

(defclass css-container []
  {:background (:container-bg @color)
   :color (:container-fg @color)}
  ["*" {:color (:container-fg @color)}])

(def Container (el-create :div css-container))
(def Div (el-create  :div))
(def Span (el-create :span))

(defclass css-bold []
  {:font-weight :bold})

(def Em (el-create :em css-bold))

(defclass css-anchor []
  {:text-decoration "underlined"})

(def A (el-create :a css-anchor))

(defclass css-button []
  {:background (:button-main @color)
   :color "#000"
   :padding "5px"
   :border "none"
   }
  [:&:hover :&:focus
   {:background (:button-focus @color) }]
  [:&:active
   {:background (:button-active @color) }]) 
(def Button (el-create :button css-button))
(def Submit (el-create :input css-button {:type "submit"}))
(def Goto (el-create :a css-button))

(defclass css-clearfix []
  {:content ""
   :clear "both"})

(def Clearfix (el-create :span css-clearfix))


(defn Element [type option attribute & children]
  )
