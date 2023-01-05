(ns supervisor.data.theme
  (:require
    [re-frame.core :as reframe]
    [supervisor.style :as style]
    [supervisor.util :as util]))

(def default-px-header-height (style/px 40))
(def default-px-main-height (style/calc :100vh :- default-px-header-height))

(def default-size
  {:header-height default-px-header-height
   :main-height default-px-main-height})

(def default-pallet
  {:container-bg :#deddda
   :black :#000000
   :white :#ffffff
   :bg :#deddda
   :container-fg :#000000
   :fg :#000000
   :button-main :#B9C09E
   :button-selected :#B9C09E
   :input-focus :#63b3cc
   :blue-dot :#6eaced
   :button-debug :#a0afbf
   :storybook-panel :#74b287
   :storybook-nav-unselected :#4da568
   :storybook-nav-selected :#c0db97
   :pending-1 :#3a6f72
   :pending-2 :#6c9684
   :header-bg :#68cc86
   :grey :#999999
   :broken :#ea2517 ; this color will appear if css is broken
   })

(reframe/reg-event-db
  :size-set
  (fn [db [_ size]]
     (assoc-in db [:theme :size] size)))

(reframe/reg-event-db
  :pallet-set
  (fn [db [_ pallet]]
     (assoc-in db [:theme :pallet] pallet)))

(defn pallet-set
  [pallet]
  (reframe/dispatch [:pallet-set pallet]))

(defn size-set
  [size]
  (reframe/dispatch [:size-set size]))

(reframe/reg-sub
  :theme-fetch
  (fn [db] (:theme db) ))

(reframe/reg-sub
  :pallet-fetch
  (fn [db] (get-in db [:theme :pallet]) ))

(defn fetch
  []
  (reframe/subscribe [:theme-fetch]))

(defn fetch-pallet
  []
  (reframe/subscribe [:pallet-fetch]))

(defn init
  []
  (reframe/dispatch-sync [:size-set default-size])
  (reframe/dispatch-sync [:pallet-set default-pallet]))
