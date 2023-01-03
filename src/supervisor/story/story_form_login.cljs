(ns supervisor.story.story-form-login
  (:require
    [spade.core :as spade]
    [re-frame.core :as reframe]
    [fork.core :as fork]
    [supervisor.data.access-token :as access-token]
    [supervisor.unit.form-login :refer [unit-form-login]]
    [supervisor.rand :as random]
    [supervisor.base :as <>]))

(spade/defclass css-story-unit-form-login []
   [:.about
    {:background :black
     :color :white
     :padding :10px
     :margin-bottom :10px
     :height :125px
     }]
   [:h2 {:background :#999999
         :padding :10px
         :margin [[:20px :0px]]}]
   [:button {:margin-top :10px
             :margin-right :10px}])

(reframe/reg-event-db
  ::form-login-clear-server-message
  (fn [db]
    (fork/set-server-message db :form-login nil)))

(defn- clear-server-message []
  (reframe/dispatch [::form-login-clear-server-message]))

(defn story-unit-form-login []
  (let [token  @(access-token/fetch)
        has-access-token (boolean token)
        email (random/email)
        password (random/password)]
    [:div {:class (css-story-unit-form-login)}
     [:div.about
       [:h1 "story unit-form-login" ]
       [:p "ldb has auth-token: " (str has-access-token)]
       [<>/ButtonDebug
        {:on-click #(clear-server-message) }
        "clear error message"]
       [<>/ButtonDebug
        {:on-click #(access-token/write nil)
         :disabled (not has-access-token)}
        "clear access-token"]
       ]
     ; show inital
     [:h2 "empty"]
     [unit-form-login]
     [:h2 "with random values"]
     [unit-form-login {:initial-values {:email email :password password}}]
     ]))
