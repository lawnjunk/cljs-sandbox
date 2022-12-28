(ns supervisor.unit.form-login
  (:require 
    [spade.core :as spade]
    [re-frame.core :as reframe]
    [fork.re-frame :as fork]))

(reframe/reg-event-fx 
  :form-login-submit-handler
  (fn [cofx [_ {:keys [values dirty path]}]]
    (println "values" values)
    (println "dirty" dirty)
    (println "path" path)

    ))


; TODO choose a validation library for deling with forms
; + bonus points for lib if there is utility suplamenting fx

(defn- form-login 
  [{:keys [handle-submit submitting? values handle-change handle-blur]}]
  [:form
   {:on-submit handle-submit}
    [:input 
     {:name "email"
      :type "email"
      :placeholder "email"
      :value (values :email)
      :on-change handle-change
      :on-blur handle-blur} ]
    [:input 
     {:name "password"
      :type "password"
      :placeholder "password"
      :value (values :password)
      :on-change handle-change
      :on-blur handle-blur}]
    [:button.submit-login
     {:type "submit" 
      :disabled submitting?
      }
     "submit"
     ]]

  )

(defn unit-form-login 
  [opt]
  [:div 
   [fork/form 
    {:path [:form-login]
     :keywordize-keys true
     :prevent-default? true
     :clean-on-unmount? true
     :on-submit #(println "fuuuk" %)
     :initial-values (:initial-values opt)}
    form-login]])

(spade/defclass css-story-unit-form-login
  []
  [:.about 
   {:background :black
    :color :white
    :padding :10px
    :margin-bottom :10px
    }
   ])

(defn story-unit-form-login []
  (let []
    [:div {:class (css-story-unit-form-login)}
     [:div.about 
       [:h1 "story unit-form-login" ]     
       [:p "auth token set: " (str false)]]
     [unit-form-login 
      {:initial-values 
       {:email "duncan@kiipo.com" :password "123456"}}]
     ]))
