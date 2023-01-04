(ns supervisor.unit.form-login
  (:require
    [supervisor.util :as util]
    [reagent.core :as reagent]
    [spade.core :as spade]
    [re-frame.core :as reframe]
    [fork.re-frame :as fork]
    [supervisor.base :as <>]
    [supervisor.side.ldb :as ldb]
    [supervisor.style :as style]
    [supervisor.http.authenticate :as authenticate]
    [supervisor.data.access-token :as access-token]
    [supervisor.data.request-ctx :as request-ctx]))

; TODO STYLE ME
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
    {:db (fork/set-submitting db :form-login true)
     :api (authenticate/api-fx
            (merge values {:request-id request-id })) })))

(spade/defclass css-wat []
  {:background :red})

; TODO choose a validation library for deling with forms
; + bonus points for lib if there is utility suplamenting fx
; and add custom warning bubble if dirty
(defn- part-form
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
      [<>/ButtonSubmit {:type "submit" :disabled submitting?} "submit" ]
      ])
  )

(defn unit
  "form-login

  optional-props
    :initial-values (a map with email password for testing)
  "
  [props]
  (let [request-id (util/id-atom)]
    (fn []
      (let [ctx @(request-ctx/fetch @request-id)
            pending (when ctx (:pending ctx))
            initial-values (:initial-values props)
            props (dissoc props :initial-values)
            ]
        [:div (style/merge-props props {:class (css-unit-form-login)})
         [fork/form
          {:path [:form-login]
           :keywordize-keys true
           :prevent-default? true
           :clean-on-unmount? true
           :on-submit #(reframe/dispatch [:form-login-submit-handler % @request-id])
           :initial-values initial-values}
          part-form]
         (when pending [:p {:style {:text-align :center :color :blue}} "... request pending ..."])
         ]))))
