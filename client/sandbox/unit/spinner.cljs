(ns sandbox.unit.spinner
  (:require
    [reagent.core :as reagent]
    [sandbox.data.request-ctx :as request-ctx]
    [sandbox.http.request :refer [request]] 
    [sandbox.base :as <>]
    [sandbox.util :as util]))

(defn http-spinner [request-id]
  (request
    {:url "/api/spinner"
     :request-id request-id
     }))

(defn unit-spinner []
  (let [request-id (reagent/atom (util/genid))]
    (http-spinner @request-id)
    (reagent/create-class
      {:component-will-unmount #(request-ctx/abort @request-id)
       :reagent-render
         (fn []
           (let [req-ctx @(request-ctx/fetch @request-id)]
             (println "req-ctx" req-ctx)
             [:div
              (if-not req-ctx
                [:p "no req ctx found"]
                (let [pending (str (:pending req-ctx))
                      success (str (:is-success req-ctx))
                      res-data (util/xxdp "res-data" (get req-ctx :res-data {}))
                      content (or (get res-data :content)
                                  (get-in req-ctx [:error :content]))
                      imageUrl (get res-data :imageUrl) ]
                  [:div
                   (if (:pending req-ctx)
                     [<>/Button {:on-click #(request-ctx/abort @request-id)} "abort"]
                     [<>/Button {:on-click #(http-spinner @request-id)} "request"]
                     )
                   [:p "pending: " pending]
                   [:p "success: " success]
                   [:p "content: " content]
                   (if imageUrl 
                     [:img
                      {:src imageUrl
                       :alt content }])]))]))})))
