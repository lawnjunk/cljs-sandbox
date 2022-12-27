(ns sandbox.main
  (:require
    [reagent.dom]
    [re-frame.core :as reframe]
    [secretary.core :as secretary]
    [sandbox.util :as util]
    [sandbox.location :as location]
    [sandbox.unit.page-router :refer [unit-page-router]]
    [sandbox.unit.header :refer [unit-header]]
    [sandbox.side.qdb :as qdb]
    [sandbox.side.ldb :as ldb]
    [sandbox.side.debug]
    ))

; (reframe/reg-sub :all (fn [db] db))
; (println  (reframe/subscribe[:all]))

;TODO create a way to detect if an element is in the viewport or not
; and create hooks didEnterViewport didLeaveViewport
; and maby allow for triggers above and below (500px up and down)
; and create hook couldEnterViewport (near viewport up or down)
(defn app []
  (let [qdb @(qdb/fetch)
        ldb @(ldb/fetch)]
    (util/xxp "ldb" ldb)
    (util/xxp "qdb" qdb)
    [:div.sandbox-container
     [unit-header]
     [unit-page-router]]))

(defn render []
  (println "main/render")
  (let [container (js/document.getElementById "sandbox-container")]
    (reagent.dom/render [app] container)))

(defn ^:dev/before-load before-load 
 "hook that runs when before shadow watch reloads"
  []
  (util/xxl "main/before-load"))

(defn ^:dev/after-load after-load
  "hook that runs after shadow watch reloads"
  []
  (util/xxl "main/after-load" 
  (reframe/clear-subscription-cache!)
  (render)))

(defn init
  "init will trigger once on page refresh"
  []
  (util/xxl "main/init ")
  (qdb/hydrate!)
  (println "route" (location/fetch-route))
  (secretary/dispatch! (location/fetch-route))
  (.addEventListener js/window "popstate"
    (fn [] (secretary/dispatch! (location/fetch-route))))
  (ldb/hydrate!)
  (after-load))
