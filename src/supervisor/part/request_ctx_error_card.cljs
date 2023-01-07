 (ns supervisor.part.request-ctx-error-card
  (:require
    [spade.core :as spade]
    [supervisor.data.theme :as d-theme]
    [supervisor.unit.blue-dot :as blue-dot]
    [supervisor.style :as style]
    [supervisor.util :as util]
    [supervisor.fake :as fake]
    [supervisor.space :as s]
    [supervisor.base :as b]))

(spade/defclass css-request-ctx-error-card
  []
  (let [pallet @(d-theme/fetch-pallet)
        bg-color (:broken pallet)
        fg-color (:black pallet)
        ]
    [:&
     {:padding :10px
      :border [[ :4px :solid bg-color]]
      :color :red
      :position :relative
      :background (style/opacify 0.2 bg-color )
      }
     [:.blue-dot
      {:position :absolute
       :background-alpha 0.1
       :top :5px
       :right :5px}]
     [:p {:color fg-color }]
     ]))

(defn part
  "css-request-ctx-error-card"
  [request-ctx]
  (let [request-ctx (dissoc request-ctx :ajax)
        pending (:pending request-ctx)
        is-success (:is-success request-ctx)
        error-message (get-in request-ctx [:error :message] "unknown error")]
    (when (not pending)
      (when (not is-success)
        [s/box (style/tag (css-request-ctx-error-card))
         [:h2 "Request Error"]
         [blue-dot/unit {:data request-ctx}]
         [:p error-message]
          ]))))
