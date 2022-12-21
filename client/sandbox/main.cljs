(ns sandbox.main
  (:require
    ["uuid" :as uuid]
    [clojure.pprint :as pp]
    [clojure.walk :refer [keywordize-keys]]
    [ajax.core :refer [POST]]
    [reagent.dom]
    [garden.color :as color]
    [reagent.core :as reagent]
    [re-frame.core :as reframe]
    [secretary.core :as secretary]
    [clojure.string :as s]
    [sandbox.base :as <>]
    [sandbox.util :as util]
    [sandbox.util :refer [xxl xxp xxdp]]
    [sandbox.page.storybook :refer [page-storybook]]
    [sandbox.unit.counter :refer [unit-counter]]
    [sandbox.unit.header :refer [unit-header]]
    [sandbox.data.request-ctx :as request-ctxx]
    [sandbox.data.request-ctx :as request-ctx]
    [sandbox.data.route :as route]
    [oops.core :as oops]
    [goog.events])
  (:require-macros
    [secretary.core :refer [defroute]])
  (:import
    goog.History
    goog.History.EventType))

; (reframe/reg-sub :all (fn [db] db))
; (println  (reframe/subscribe[:all]))

(defn page-landing []
  [:div
   [:h1  "this is a placehoder page" ]
   ])


;TODO create a way to detect if an element is in the viewport or not
; and create hooks didEnterViewport didLeaveViewport
; and maby allow for triggers above and below (500px up and down)
; and create hook couldEnterViewport (near viewport up or down)

(defroute route-page-storybook "/storybook" [query-params]
  (route/goto :storybook query-params nil))

(defroute route-page-storybook-selected "/storybook/:id" [id query-params]
  (println "googooogoo" id query-params )
  (route/goto :storybook query-params id))

(defroute route-landing "*" [query-params]
  (route/goto :landing query-params nil))

(defn app []
  (let [route @(route/fetch)]
    (println "route " route)
    [<>/Container { }
     [unit-header]
     (case (:page route)
       :storybook [page-storybook (:query route) (:id route)]
       [page-landing])
   ]))

(defn render []
  (let [container (js/document.getElementById "sandbox-container")]
    (reagent.dom/render [app] container)))

(defn ^:dev/before-load stop []
  (xxl "before-load stop"))

(defn ^:dev/after-load render-app []
  (xxl "after-load start") 
  (reframe/clear-subscription-cache!)
  (render))

(defn init []
  (xxl "init ")
  ; if window.location is not using a hashroute force it to use a hash
  (let [hashroute-load (.-hash js/window.location)
        pathname (.-pathname js/window.location)
        hashroute (str "/#" pathname)]
    (if (= "" hashroute-load)
      (oops/oset! js/window ".location" hashroute)))
  (secretary/set-config! :prefix "#")
  (secretary/dispatch! (util/location-get))
  (js/window.addEventListener "hashchange" #(secretary/dispatch! (str (util/location-get))))
  (reframe/dispatch [:set-counter -666])
  (render-app))

