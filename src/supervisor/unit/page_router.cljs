(ns supervisor.unit.page-router
  (:require
    [spade.core :as spade]
    [secretary.core :refer [defroute]]
    [supervisor.style :as style]
    [supervisor.util :as util]
    [supervisor.page.storybook :refer [page-storybook]]
    [supervisor.page.loading :refer [page-loading]]
    [supervisor.data.theme :as d-theme]
    [supervisor.data.route :as d-route]))

(spade/defclass css-unit-page-router [theme]
  (let [header-height (get-in theme [:size :header-height])]
    [:&
     {:width :100vw
      :height (util/css-calc :100vh :- header-height)}]))

(defroute route-page-storybook "/storybook" [query-params]
  (d-route/goto :storybook query-params nil))

(defroute route-page-storybook-selected "/storybook/:id" [id query-params]
  (d-route/goto :storybook query-params id))

(defroute route-landing "*" [query-params]
  (d-route/goto :landing query-params nil))

(defn unit-page-router [] ()
  (let [theme @(d-theme/fetch)
        route @(d-route/fetch)]
    [:div.page-router {:class (css-unit-page-router theme)}
      (case (:page route)
        :storybook [page-storybook route]
        [page-loading])]))
