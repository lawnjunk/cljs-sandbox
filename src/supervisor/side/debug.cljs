(ns supervisor.side.debug
  (:require
    [re-frame.core :as reframe]
    [supervisor.util :as util]))

; reg-event-fx shouldn't be used for side effect
; but this is just for debug so ¯\_(ツ)_/¯
(reframe/reg-event-fx
  :debug
  (fn [_ [_ & args]]
    (apply println (util/vconcat [:debug-event] args))))

