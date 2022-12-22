(ns sandbox.main
  (:require
    [reagent.dom]
    [re-frame.core :as reframe]
    [secretary.core :as secretary]
    [sandbox.util :as util]
    [sandbox.unit.page-router :refer [unit-page-router]]
    [sandbox.unit.header :refer [unit-header]]
    [oops.core :as oops]
    [goog.events]))

; (reframe/reg-sub :all (fn [db] db))
; (println  (reframe/subscribe[:all]))

;TODO create a way to detect if an element is in the viewport or not
; and create hooks didEnterViewport didLeaveViewport
; and maby allow for triggers above and below (500px up and down)
; and create hook couldEnterViewport (near viewport up or down)
(defn app []
  [:class.sandbox-container
   [unit-header]
   [unit-page-router]
   [:p :hello (.random js/Math)]
   ])

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
  "init will trigger on page refresh"
  []
  (util/xxl "main/init ")
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
  (after-load))

