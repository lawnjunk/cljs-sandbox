(ns sandbox.unit.spinner
  (:require
    ; [reagent.core :as reagent]
    [sandbox.data.request-ctx :as request-ctx]
    [sandbox.http.spinner:refer :refer [http-spinner]] 
    [sandbox.base :as <>]
    [sandbox.util :as util]))

(defn unit-spinner []
  (let [request-id (util/id-atom)]
    (<>/Element
      {:component-will-unmount #(request-ctx/abort-and-delete @request-id)
       :component-did-mount #(http-spinner @request-id)
       :render
         (fn []
           (let [req-ctx @(request-ctx/fetch @request-id)]
             [:div
              (if-not req-ctx
                [:div
                 [<>/Button {:on-click #(http-spinner @request-id)} "request"]
                 [:p "no req ctx found"]]
                (let [pending (str (:pending req-ctx))
                      success (str (:is-success req-ctx))
                      res-data (get req-ctx :res-data {})
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
