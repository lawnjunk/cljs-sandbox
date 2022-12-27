(ns sandbox.unit.page-router
  (:require 
    [spade.core :as spade]
    [secretary.core :refer [defroute]]
    [sandbox.style :as style]
    [sandbox.util :as util]
    [sandbox.page.storybook :refer [page-storybook]]
    [sandbox.page.loading :refer [page-loading]]
    [sandbox.data.route :as route]))

(spade/defclass css-unit-page-router []
  {:width :100vw
   :height (util/css-calc :100vh :- style/size-px-header-height)})

(defroute route-page-storybook "/storybook" [query-params]
  (route/goto :storybook query-params nil))

(defroute route-page-storybook-selected "/storybook/:id" [id query-params]
  (route/goto :storybook query-params id))

(defroute route-landing "*" [query-params]
  (route/goto :landing query-params nil))

(defn unit-page-router [] ()
  (let [route @(route/fetch)]
    (println :route route)
    [:div.page-router {:class (css-unit-page-router)}
      (case (:page route)
        :storybook [page-storybook route]
        [page-loading])]))
