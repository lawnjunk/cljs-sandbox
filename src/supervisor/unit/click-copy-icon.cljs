(ns supervisor.unit.click-copy-icon
  (:require
    [supervisor.util :as util]))

(defn unit-click-copy-icon
  [options]
  (let [text (:text options)]
    [:button
     {:on-click #(util/copy-to-clipboard text)}]))



