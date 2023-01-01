(ns supervisor.base
  (:require
    [reagent.core :as reagent]
    [secretary.core :as secretary]
    [garden.color :as color]
    [spade.core :refer [defclass]]
    [supervisor.style :refer [pallet]]
    [supervisor.location :as location]
    [clojure.string :as string]
    [supervisor.util :as util]))

(defn- el-create-class-option
  [options style]
  (let [spade-class (style)
        options-class (:class options)
        class-list [spade-class options-class]] 
    (string/join " " class-list)))

(defn- el-create-no-css
  [tag-name ]
  (fn [options & children]
    (util/vconcat [tag-name options] children)))

(defn- el-create-with-css
  [tag-name style]
  (fn [options & children]
    (util/vconcat [tag-name
                   (merge
                     options
                     {:class (el-create-class-option options style)})]
                  children)))

(defn- el-create-with-attribute
  [tag-name style attribute]
  (fn [options & children]
    (util/vconcat [tag-name
                   (merge
                     attribute
                     options
                     {:class (el-create-class-option options style)})] 
                  children)))

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

(defclass css-button 
  [options]
  (let [pallet (or (:pallet options) @pallet)
        primary-color (:button-main pallet)
        selected-color (:button-selected pallet)] 
    [:& {:background (:button-main pallet) 
         :color "#000"
         :padding "5px"
         :border "none"
         :cursor :pointer}
        [:&:focus
         {:background (color/darken primary-color 5)}]
        [:&:hover
         {:background (color/darken primary-color 10)} ]
        [:&:active
         {:background (color/darken primary-color 15)} ]
        [:&:disabled
         {:background  (color/lighten primary-color 5)
          :color :#999999
          :cursor :auto}]
        [:&.seleced 
         [:&:focus
          {:background (color/darken selected-color 5)}]
         [:&:hover
          {:background (color/darken selected-color 10)} ]
         [:&:active
          {:background (color/darken selected-color 15)} ]
         [:&:disabled
          {:background  (color/lighten selected-color 5)
           :color :#999999
           :cursor :auto
           }]



    {:background selected-color}]])
  )

(def Button (el-create :button css-button))


(def button-debug-css-options
  {:pallet  { :button-main "#9cb8ed" :button-selected "#a3dbe0" }})

(def ButtonDebug (el-create :button (partial css-button button-debug-css-options)))
(def Submit (el-create :input css-button {:type "submit"}))

(defn Hpush[opts & children]
  (let [href (:href opts)]
    (util/vconcat [:a (merge opts {:on-click 
         (fn [e]
           (.preventDefault e)
           (location/push-pathname! href)
           )})]
            children)))

(defn Hreplace[opts & children]
  (let [href (:href opts)]
    (util/vconcat [:a (merge opts {:on-click 
         (fn [e]
           (.preventDefault e)
           (location/replace-pathname! href)
           )})]
            children)))

(defclass css-clearfix []
  {:content ""
   :clear "both"})

(def Clearfix (el-create :span css-clearfix))

(def Element reagent/create-class)

