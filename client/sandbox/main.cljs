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
    [sandbox.data.simple-store :refer [simple-store-create]])
  (:require-macros
    [secretary.core :refer [defroute]]))

; (reframe/reg-sub :all (fn [db] db))
; (println  (reframe/subscribe[:all]))

(defonce store-route (simple-store-create :route {:page "/" :args []}))
(defonce store-spinner (simple-store-create :spinner-content "spinner-content-nothing"))

(defn page-landing []
  [:div
   [:h1  "this is a placehoder page" ]
   ])

(secretary/set-config! :prefix "#")

(defroute route-page-storybook "/storybook" [_ query]
  ((:set store-route) {:page :storybook :args [nil query]}))

(defroute route-page-storybook-selected "/storybook/:id" [id query]
  ((:set store-route) {:page :storybook :args [id query]}))

(defroute route-landing "*" []
  ((:set store-route) {:page :landing :args []}))

(defn app []
  (let [route @((:get store-route))]
    (println "route " route)
    [<>/Container { }
     [unit-header]
     (case (:page route)
       :storybook (util/vconcat [page-storybook] (:args route))
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
  (secretary/dispatch! (util/location-get))
  (js/window.addEventListener "hashchange" #(secretary/dispatch! (util/location-get)))
  (xxl "init ")
  (reframe/dispatch [:set-counter -666])
  (render-app))

     ; (map #(identity [<>/Button
     ;                  {:key (str "wat" %)
     ;                   :on-click (partial println "haha" %)}
     ;                  (str "clickr # " %)])
     ;      (range 0 10))
