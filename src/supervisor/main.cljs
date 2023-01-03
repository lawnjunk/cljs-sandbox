(ns supervisor.main
  (:require
    [reagent.dom]
    [re-frame.core :as reframe]
    [secretary.core :as secretary]
    [supervisor.util :as util]
    [supervisor.location :as location]
    [supervisor.unit.page-router :as page-router]
    [supervisor.unit.header :as header]
    [supervisor.data.theme :as theme]
    [supervisor.side.qdb :as qdb]
    [supervisor.side.ldb :as ldb]
    [supervisor.side.debug]
    [supervisor.environ]
    [supervisor.side.api]
    ))

; TODO add date format utils to supervisor.util
; TODO add pretty-bytes and pretty-ms to supervisor.util

; (reframe/reg-sub :all (fn [db] db))
; (println  (reframe/subscribe[:all]))

;TODO create a way to detect if an element is in the viewport or not
; and create hooks didEnterViewport didLeaveViewport
; and maby allow for triggers above and below (500px up and down)
; and create hook couldEnterViewport (near viewport up or down)
(defn app []
  ; do setup?
  (fn []
    (let [qdb @(qdb/fetch)
          ldb @(ldb/fetch)]
      (util/xxp "ldb" ldb)
      (util/xxp "qdb" qdb)
      [:div.supervisor-container
       [header/unit]
       [page-router/unit]])))

(defn render []
  (println "main/render")
  (let [container (js/document.getElementById "supervisor-container")]
    (reagent.dom/render [app] container)))

(defn ^:dev/before-load before-load
 "hook that runs when before shadow watch reloads"
  []
  (util/xxl "main/before-load")
  )

(defn ^:dev/after-load after-load
  "hook that runs after shadow watch reloads"
  []
  (util/xxl "main/after-load")
  (reframe/clear-subscription-cache!)
  (render))

(defn init
  "init will trigger once on page refresh"
  []
  (util/xxl "main/init ")
  (qdb/hydrate!)
  (ldb/hydrate!)
  (theme/init)
  (println "route" (location/fetch-route))
  (secretary/dispatch! (location/fetch-route))
  (.addEventListener js/window "popstate"
    (fn [] (secretary/dispatch! (location/fetch-route))))

  ; set inital state (must use dispatch-sync
  (after-load))
