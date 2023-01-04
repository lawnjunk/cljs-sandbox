(ns supervisor.book.story-form-login
  (:require
    [spade.core :as spade]
    [re-frame.core :as reframe]
    [fork.core :as fork]
    [supervisor.data.access-token :as d-access-token]
    [supervisor.data.theme :as d-theme]
    [supervisor.unit.form-login :as form-login]
    [supervisor.fake :as fake]
    [supervisor.base :as <>]))

(spade/defclass css-story-unit-form-login []
  (let [pallet @(d-theme/fetch-pallet) ]
    [:&
     [:.about
      {:background (:black pallet)
       :color :white
       :padding :10px
       :margin-bottom :10px
       :height :125px
       }]
     [:h2 {:background (:grey pallet)
           :padding :10px
           :margin [[:20px :0px]]}]
     [:button {:margin-top :10px
               :margin-right :10px}]]))

(reframe/reg-event-db
  ::form-login-clear-server-message
  (fn [db]
    (fork/set-server-message db :form-login nil)))

(defn- clear-server-message []
  (reframe/dispatch [::form-login-clear-server-message]))

(defn story-unit-form-login []
  (let [access-token  @(d-access-token/fetch)
        has-access-token (boolean access-token)
        email (fake/email)
        password (fake/password)]
    [:div.story {:class (css-story-unit-form-login)}
     [:div.about
       [:h1 "story unit-form-login" ]
       [:p "ldb has auth-token: " (str has-access-token)]
       [<>/ButtonDebug
        {:on-click #(clear-server-message) }
        "clear error message"]
       [<>/ButtonDebug
        {:on-click #(d-access-token/write nil)
         :disabled (not has-access-token)}
        "clear access-token"]
       ]
     [:div.main
     ; show inital
     [:h2 "empty"]
     [form-login/unit]
     [:h2 "with random values"]
     [form-login/unit {:initial-values {:email email :password password}}]]
     ]))
