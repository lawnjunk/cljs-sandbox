(ns supervisor.main
  (:require
    [oops.core :as oops]
    ["@faker-js/faker" :refer [faker]]

    [reagent.dom]
    [re-frame.core :as reframe]
    [secretary.core :as secretary]
    [supervisor.util :as util]
    [supervisor.location :as location]
    [supervisor.unit.page-router :refer [unit-page-router]]
    [supervisor.unit.header :refer [unit-header]]
    [supervisor.side.qdb :as qdb]
    [supervisor.side.ldb :as ldb]
    [supervisor.side.debug]
    [supervisor.environ]
    ))

; TODO make a supervisor.fake with a bunch of fns for faking data
; word line email hex image-url hex-color city country name
; bool float int date-past date-future date-between adjective
; password

; TODO add date format utils to supervisor.util
; TODO add pretty-bytes and pretty-ms to supervisor.util

(util/xxl "FAKE WORD"  ((oops/oget faker "random.words") 5))

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
    [:div.supervisor-container
     [unit-header]
     [unit-page-router]]))

(defn render []
  (println "main/render")
  (let [container (js/document.getElementById "supervisor-container")]
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
