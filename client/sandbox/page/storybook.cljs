(ns sandbox.page.storybook
  (:require
    [reagent.core :as reagent]
    [sandbox.base :as <>]))

(def uri-page-storybook "/#/storybook")

(defn el-storybook-nav-item [selected] 
  (let [href (str uri-page-storybook "?selected=" selected)] 
    [<>/Goto {:href href} selected ]))

; TODO store.params.pagename ur somthing auto mod querystring
; * param-set [page-name param value]
; * param-get [page-name] -> map of pages params
; * perhaps control with [Select {:option value}]

; TODO siik storybook mods
; * toggle :main.content size (small, med, large)
; * toggle :nav side or bottom
; * toggle :theme-dark :theme-light

(defn page-storybook []
  (let [selected (reagent/atom nil)]
    [:div
     [:header]
     [:section.left-panel
      [:nav
       [el-storybook-nav-item "boring"]
       [el-storybook-nav-item "interesting"]
       [el-storybook-nav-item "spinner"]
       ]]
     [:main.content "well fuck yo"]]))
