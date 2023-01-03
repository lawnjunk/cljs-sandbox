(ns supervisor.data.route
  (:require
    [secretary.core :as secretary]
    [supervisor.page.storybook :as storybook]
    [supervisor.page.loading :as loading]
    [supervisor.util :as util]
    [re-frame.core :as reframe]))

(def route-default
  {:page :landing :id nil :query {}})

(reframe/reg-event-db
  :route-goto
  (fn  [db [_ route-data]]
    (assoc db :route route-data)))

(defn goto [route-data]
  (reframe/dispatch [:route-goto route-data]))

(reframe/reg-sub
  :route-fetch
  (fn [db]
    (get db :route route-default)))

(defn fetch []
  (reframe/subscribe [:route-fetch]))

(defonce storybook-route
  {:tag (util/id-gen)
   :page :storybook ;page is used for selected
   :path "/storybook"
   :has-id false
   :show-as-nav-link true
   :link-name "storybook"
   :link-href "/storybook"
   :view storybook/page })

(defonce storybook-id-route
  (merge
    storybook-route
    {:tag (util/id-gen)
     :path "/storybook/:id"
     :has-id true
     :show-as-nav-link false
     }))

(defonce home-route
  {:tag (util/id-gen)
   :page :landing
   :path "*"
   :show-as-nav-link true
   :link-name "home"
   :link-href "/"
   :view loading/page
   })

; order of route-list matters for nav-bar link order
(def route-list
  [home-route
   storybook-route
   storybook-id-route ])

; AHHHHH WHY WHY WHY CAN THIS LIB NOT BE DYNAMIC? (GRRRRRRR)
(secretary/defroute s-storybook-route (:path storybook-route) [query-params]
  (goto (merge storybook-route {:id nil :query query-params})))

(secretary/defroute s-storybook-id-route (:path storybook-id-route) [id query-params]
  (goto (merge storybook-id-route {:id id :query query-params})))

(secretary/defroute s-home-route "*" [query-params]
  (goto (merge home-route {:id nil :query query-params})))
