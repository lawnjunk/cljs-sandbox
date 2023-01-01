(ns supervisor.unit.form-login
  (:require
    [spade.core :as spade]
    [re-frame.core :as reframe]
    [fork.re-frame :as fork]
    [supervisor.base :as <>]
    [supervisor.side.ldb :as ldb]
    [supervisor.util :as util]
    [supervisor.data.access-token :as access-token]
    [supervisor.data.request-ctx :as request-ctx]))

(spade/defclass css-unit-form-login []
  [:input 
   {:margin-bottom :10px}]
  [:.server-message-container 
   {:height :75px}]
  [:.server-message
   {:display :inline-block
    :background :green
    :width :auto
    :color :white
    :margin-bottom :10px
    :padding :10px}
   [:&.error {:background :red}]
   ])

; TODO move into http/authenticate
(reframe/reg-event-fx
  :api-v1-supervisor-authenticate
  [(ldb/inject)]
  (fn [cofx [_ request-ctx]]
    (let [db (:db cofx)
          success (:is-success request-ctx)
          res-data (:res-data request-ctx)
          token (get-in res-data [:payload :accessToken])]
      (if success
        {:db (-> db (fork/set-submitting :form-login false))
         :fx [(access-token/write-fx token)]}
        {:db (-> db
                   (fork/set-submitting :form-login false)
                   (fork/set-server-message
                     :form-login
                     {:type :error
                      :message "The server was not able to authorize your credentials."}))}))))

(reframe/reg-event-fx
  :form-login-submit-handler
  (fn [cofx [_ {:keys [values dirty path]} request-id]]
    (let [db (:db cofx)]
    (println "request-id" request-id) 
    (println "values" values)
    (println "dirty" dirty)
    (println "path" path)
    {:db (-> db
             (fork/set-submitting :form-login true))
     ; TODO move into http/authenticate authenticate-fx
     :api
     {:request-id request-id
      :route "/v1/supervisor/authenticate"
      :req-data values
      :fx [[:dispatch [:api-v1-supervisor-authenticate]]]
      }})))

; TODO choose a validation library for deling with forms
; + bonus points for lib if there is utility suplamenting fx
; and add custom warning bubble if dirty 

(defn- form-login
  [{:keys [handle-submit
           submitting?
           values
           handle-change
           handle-blur
           on-submit-server-message
           ]}]
  (let [server-message (get on-submit-server-message :message)
        server-message-type (get on-submit-server-message :type)]
    [:form
     {:on-submit handle-submit}
      [:div.server-message-container
       (when server-message
         [:span.server-message {:class (name server-message-type)} server-message])]
      [:input
       {:name "email"
        :type "text"
        :placeholder "email"
        :value (values :email)
        :disabled submitting?
        :on-change handle-change
        :on-blur handle-blur}]
      [:input
       {:name "password"
        :type "password"
        :placeholder "password"
        :value (values :password)
        :disabled submitting?
        :on-change handle-change
        :on-blur handle-blur}]
      [<>/Button
       {:type "submit"
        :disabled submitting?
        }
       "submit"
       ]
      ])
  )

(defn unit-form-login [opt]
  (let [request-id (util/id-atom)]
    (fn []
      (let [ctx @(request-ctx/fetch request-id)
            disabled (when ctx (:pending ctx)) ]
        [:div {:class (css-unit-form-login)}
         [fork/form
          {:path [:form-login]
           :keywordize-keys true
           :prevent-default? true
           :clean-on-unmount? true
           :on-submit #(reframe/dispatch [:form-login-submit-handler % @request-id])
           :initial-values (:initial-values opt)}
          form-login]]))))

(spade/defclass css-story-unit-form-login []
   [:.about
    {:background :black
     :color :white
     :padding :10px
     :margin-bottom :10px
     :height :125px
     }]
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
        has-access-token (boolean token)]
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
     [unit-form-login
      {:initial-values
       {:email "duncan@kiipo.com" :password "123456"}}]
     ]))
