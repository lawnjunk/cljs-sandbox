(ns supervisor.base
  (:require
    [reagent.core :as reagent]
    [secretary.core :as secretary]
    [spade.core :refer [defclass]]
    [supervisor.style :refer [pallet]]
    [supervisor.location :as location]
    [supervisor.util :as util]))

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
  {:background (:container-bg @pallet)
   :color (:container-fg @pallet)}
  ["*" {:color (:container-fg @pallet)}])

(def Container (el-create :div css-container))
(def Div (el-create  :div))
(def Span (el-create :span))

(defclass css-bold []
  {:font-weight :bold})

(def Em (el-create :em css-bold))

(defclass css-anchor []
  {:text-decoration "underlined"})

(defclass css-button []
  {:background (:button-main @pallet)
   :color "#000"
   :padding "5px"
   :border "none"
   }
  [:&:hover :&:focus
   {:background (:button-focus @pallet) }]
  [:&:active
   {:background (:button-active @pallet) }]) 

(def Button (el-create :button css-button))
(def Submit (el-create :input css-button {:type "submit"}))

(defn Hpush[opts & children]
  (let [href (:href opts)]
    (util/vconcat [:a (merge opts {:on-click 
         (fn [e]
           (.preventDefault e)
           (location/replace-pathname! href)
           (secretary/dispatch! (location/fetch-route))
           )})]
            children)))

(defn Hreplace[opts & children]
  (let [href (:href opts)]
    (util/vconcat [:a (merge opts {:on-click 
         (fn [e]
           (.preventDefault e)
           (location/replace-pathname! href)
           (secretary/dispatch! (location/fetch-route))
           )})]
            children)))

(defclass css-clearfix []
  {:content ""
   :clear "both"})

(def Clearfix (el-create :span css-clearfix))

(def Element reagent/create-class)

