; NOTE: adding routes ind data/route will automaticly setup the page router
(ns supervisor.unit.page-router
  (:require
    [spade.core :as spade]
    [supervisor.style :as style]
    [supervisor.util :as util]
    [supervisor.data.theme :as d-theme]
    [supervisor.data.route :as d-route]))

(spade/defclass css-unit-page-router []
  (let [theme @(d-theme/fetch)
        header-height (get-in theme [:size :header-height])]
    [:&
     {:width :100vw
      :height (style/calc :100vh :- header-height)}]))

; TODO add a 404?
(defn unit-page-router [] ()
  (let [route @(d-route/fetch)
        route-tag (get route :tag)
        route-map (into {} (map #(identity [(:tag  %) %]) d-route/route-list))
        route-view (get-in route-map [route-tag :view])
        route-404-view (:view d-route/home-route)]
    [:div.page-router {:class (css-unit-page-router)}
     ((or route-view route-404-view) route)]))
