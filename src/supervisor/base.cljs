; all CapCase components in this lib require {} props
; to be passed [<>/DivInvert {} "example" ]
(ns supervisor.base
  (:require
    [spade.core :refer [defclass]]
    [supervisor.style :as style]
    [supervisor.location :as location]
    [clojure.string :as string]
    [supervisor.data.theme :as theme]
    [supervisor.util :as util]))

(defn- el-create-class-option
  [options style]
  (let [css-theme @(theme/fetch)
        spade-class (style css-theme)
        options-class (:class options)
        class-list [spade-class options-class]]
    (string/join " " class-list)))

(defn- el-create-no-css
  [tag-name ]
  (fn [props & children]
    (util/vconcat [tag-name props] children)))

(defn- el-create-with-css
  [tag-name style]
  (fn [props & children]
    (util/vconcat [tag-name
                   (style/merge-props
                     {:class (el-create-class-option props style)}
                     props
                     )]
                  children)))

(defn- el-create-with-attribute
  [tag-name style attribute]
  (fn [props & children]
    ((el-create-with-css tag-name style) (style/merge-props attribute props) children)))

(defn- el-create
  "create a base compenet with preset styles or attributes"
  ([tag-name] (el-create-no-css tag-name))
  ([tag-name style] (el-create-with-css tag-name style))
  ([tag-name style attribute] (el-create-with-attribute tag-name style attribute)))

(defclass css-div-invert [theme]
  (let [pallet (:pallet theme)]
    [:&
     {:background (:fg pallet)
      :color (:bg pallet)} ]))

(defclass css-bold []
  {:font-weight :bold})

(defclass css-button
  [theme]
  (let [pallet (:pallet theme)
        primary-color (:button-main pallet)
        selected-color (:button-selected pallet)]
    [:& { :padding "5px"
         :border "none" }
     (style/mixin-button-color primary-color selected-color)]))

(defclass css-button-debug
  [theme]
  (let [pallet (:pallet theme)
        primary-color (:button-debug pallet)
        selected-color (:button-selected pallet)]
    [:& { :padding "5px"
         :border "none" }
     (style/mixin-button-color primary-color selected-color)]))


(defclass css-clearfix []
  {:content ""
   :clear "both"})

(defn Hpush
  "an :a tag that will use the browser history api to pushState"
  [props & children]
  (let [href (:href props)]
    (util/vconcat [:a (style/merge-props props {:on-click
         (fn [e]
           (.preventDefault e)
           (location/push-pathname! href)
           )})]
            children)))

(defn Hreplace
  "an :a tag that will use the browser history api to replaceState"
  [props & children]
  (let [href (:href props)]
    (util/vconcat [:a (style/merge-props props {:on-click
         (fn [e]
           (.preventDefault e)
           (location/replace-pathname! href)
           )})]
            children)))

(defn Hback
  "an :button tag that will use the browser history api to go back"
  [props & children]
  (util/vconcat [:button (style/merge-props props {:on-click
       (fn [e]
         (.preventDefault e)
         (location/back!)
         )})]
          children))

(def Clearfix (el-create :span css-clearfix))
(def DivInvert (el-create :div css-div-invert))
(def Em (el-create :em css-bold))
(def Button (el-create :button css-button))
(def ButtonDebug (el-create :button css-button-debug))
(def ButtonSubmit (el-create :button css-button-debug {:type "submit"}))
