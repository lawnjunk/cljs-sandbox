(ns supervisor.unit.header
  (:require
    [supervisor.util :as util]
    [spade.core :as spade]
    [supervisor.style :as style]
    [supervisor.data.theme :as d-theme]
    [supervisor.unit.header-nav :as header-nav]
    [supervisor.unit.header-request-pending-icon :as header-request-pending-icon]))

; TODO add media queries
; TODO only show nav if logged
(spade/defclass css-header []
  (let [theme @(d-theme/fetch)
        pallet (:pallet theme)
        header-height (get-in theme [:size :header-height])]
    {:background (:header-bg pallet)
     :width :100%
     :height header-height}))

(defn unit
  "header
  applicaton header"
  [props]
  [:header (style/merge-props props {:class (css-header)})
   [header-request-pending-icon/unit]
   [header-nav/unit]])
