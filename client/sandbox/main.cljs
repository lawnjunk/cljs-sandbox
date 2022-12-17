(ns sandbox.main
  (:require
    ["uuid" :as uuid]
    [sandbox.data.request-ctx :as request-ctx]
    [clojure.walk :refer [keywordize-keys]]
    [oops.core :refer [oget oset!]]
    [ajax.core :refer [POST]]
    [reagent.dom]
    [reagent.core :as reagent]
    [re-frame.core :as reframe]
    [secretary.core :as secretary]
    [spade.core :refer [defclass]]
    [clojure.string :as s]
    [sandbox.base :as <>]
    [sandbox.util :as util]
    [sandbox.util :refer [xxl xxp xxdp]]
    [sandbox.page.storybook :refer [page-storybook]]
    [sandbox.unit.counter :refer [unit-counter]]
    [sandbox.data.simple-store :refer [simple-store-create]])
  (:require-macros
    [secretary.core :refer [defroute]]))

(defonce store-lala (simple-store-create :lala "cool"))
(defonce store-route (simple-store-create :route {:page "/" :args []}))
(defonce store-spinner (simple-store-create :spinner-content "spinner-content-nothing"))

(defn req-ctx-create
  ([url]
    (req-ctx-create url (uuid/v4)))
  ([url request-id]
    {:request-id request-id
     :url url
     :pending true
     :error nil
     :success nil}))

(reframe/reg-event-db
  :req-ctx-set
  (fn [db [_ counter]]
       (let [request-id (:request-id counter)
             req-store (get db :req-store {})]
         (xxdp "store" (assoc db :req-store (assoc req-store request-id counter))))))

(reframe/reg-event-db
  :req-ctx-success
  (fn [db [_ request-id]]
       (let [req-store (get db :req-store {})
             ctx (-> (get req-store request-id)
                     (assoc :pending false)
                     (assoc :success true))] 
         (assoc db :req-store (assoc req-store request-id ctx)))))

(reframe/reg-event-db
  :req-ctx-error
  (fn [db [_ request-id]]
       (let [req-store (get db :req-store {})
             ctx (-> (get req-store request-id)
                     (assoc :pending false)
                     (assoc :success false)) ]
         (assoc db :req-store (assoc req-store request-id ctx)))))

(reframe/reg-sub
  :request-id
  (fn [db stuff]
    (get (:req-store db) (second stuff))))

(defn keywordify
  [data]
  (if (nil? data)
    nil
    (keywordize-keys data)))

; TODO request(-create) needs to work for a list of spinners
; maby should return [request-ctx-fetch request]?
(defn request
  ":request-id optional request-id
   :url api endpoint
   :payload data to send as json
   :handler"
  [data]
  (let [request-id (get data :request-id (uuid/v4))
        handler (get data :handler (partial println "DEFAULT_HANDLER:" ))
        url (str "hrequest-idp://localhost:7766" (get data :url))
        payload (get data :payload)
        ctx (req-ctx-create url request-id)]
    (println "REQUEST" url payload)
    (reframe/dispatch [:req-ctx-set ctx])
    (POST url {
               :format :json
               :response-format :json
               :params (if (nil? payload) {} payload)
               :handler (fn [response]
                          (reframe/dispatch [:req-ctx-success request-id])
                          (handler {:success true :data (keywordify response)}))
               :error-handler (fn [response]
                                (let [status (get :status response 600)
                                      success (contains? #{200 201} status)
                                      data (get response :response {})]
                                  (xxp status success data)
                                  (if success
                                    ((reframe/dispatch [:req-ctx-success request-id])
                                     (handler {:success success
                                               :data data}))
                                    ((reframe/dispatch [:req-ctx-error (xxdp request-id) response])
                                     (handler {:success success
                                               :data (keywordify data)})))))})))

(defn request-api-debug [request-id status delayInMS payload handler]
  (request
    { :request-id request-id
      :url "/api/debug"
      :handler handler
      :payload {:status status
                :delayInMS delayInMS
                :payload payload}}))

; (def el-spinner-request-id (reagent/atom (uuid/v4)))

(defn req-spinner-test [request-id]
  (request-api-debug request-id 200 2000 {:content "ping pong"} 
             (fn [result]
               (let [{success :success data :data} result]
                 (println "new data" data)
                 (if success
                   ((:set store-spinner) (get data :content "gooo"))
                   ((:set store-spinner) "THERE WAS AN ERROR"))))))


(defn el-spinner-test []
  (let [request-id (reagent/atom (uuid/v4))]
    (fn []
      (let [content @((:get store-spinner))
            content (if (nil? content) "no content" content)
            req-ctx @(reframe/subscribe [:request-id @request-id])]
        [:div
         [:p content]
         [<>/Button {:on-click (partial req-spinner-test @request-id)} "click me"]
         (if (not (nil? req-ctx))
           (let [pending (:pending req-ctx)
                 pending-info (str pending)
                 success (:success req-ctx)
                 success-info (if success  (str success) "unknown")]
             [:div
               [:p "pending: " pending-info]
               [:p "success: " success-info]
               (if (not pending)
                 [:div content])]))]))))


(defn page-1 []
  [:div "page 1 is a humble simple page"])

(defn page-2 [hash-id]
  [:div "page 2 has a hash-id" " " hash-id])

(secretary/set-config! :prefix "#")

(defroute route-page-storybook "/storybook" [_ query]
  ((:set store-route) {:page :storybook :args [query]}))

(defroute route-page-2-empty "/goop" []
  ((:set store-route) {:page :second :args ["none project seleced"]}))

(defroute route-page-2 "/goop/:id" [id]
  ((:set store-route) {:page :second :args [id]}))

(defroute route-page-1 "*" []
  ((:set store-route) {:page :landing :args []}))

(defn app []
  (let [route ((:get store-route))]
    (println "route" route)
    [<>/Container { }
     (case (:page route)
       :landing [page-1]
       :second (util/vconcat [page-2] (:args route))
       :storybook (util/vconcat [page-storybook] (:args route))
       [page-1])
     [:h1 "app"]
     [unit-counter]
     [el-spinner-test]
     [<>/Goto {:href "/#/storybook" } "storybook"]
     [:a {:href "/#/beans"} "goto bean"]
     [:a {:href "/#/goop "} "goto goop"]
     [:a {:href "/#/goop/zip123 "} "goto goop goop"]
     [<>/Button {:on-click #(println "i was clicked" (js/Math.random))} "HEELO"]
     [<>/Button {} "bad beans!!!"]
     [<>/Button {} "multi hey " " children"]
     (map #(identity [<>/Button
                      {:key (str "wat" %)
                       :on-click (partial println "haha" %)}
                      (str "clickr # " %)])
          (range 0 10))
   ]))


(defn render []
  (let [container (js/document.getElementById "sandbox-container")]
    (reagent.dom/render [app] container)))

(defn ^:dev/before-load stop []
  (xxl "before-load stop"))

;; request
(defn req-item-list-fetch []
  (request
    { :url "/api/item-list-fetch"}))

(defn req-item-create [name]
  (request
    { :url "/api/item-create"
      :payload { :name name }}))

(defn ^:dev/after-load render-app []
  (xxl "after-load start") 
  (reframe/clear-subscription-cache!)
  (render))

(defn init []
  (secretary/dispatch! (util/location-get))
  (js/window.addEventListener "hashchange" #(secretary/dispatch! (util/location-get)))
  (xxl "init ")
  (reframe/dispatch [:set-counter -666])
  (render-app))

