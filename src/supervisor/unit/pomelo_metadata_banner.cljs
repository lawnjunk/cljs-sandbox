(ns supervisor.unit.pomelo-metadata-banner
  (:require
    [supervisor.http.pomelo-metadata-fetch :as pomelo-metadata-fetch]
    [supervisor.data.pomelo-metadata :as d-pomelo-metadata]
    [supervisor.data.request-ctx :as d-request-ctx]
    [supervisor.unit.blue-dot :as blue-dot]
    [clojure.string :as string]
    [spade.core :as spade]
    [supervisor.style :as style]
    [re-frame.core :as reframe]
    [reagent.core :as reagent]
    [goog.string :as gstring]
    [goog.string.format]
    [supervisor.util :as util]
    [supervisor.space :as s]
    ))

(spade/defclass css-pomelo-metadata-banner
  []
  [:&
   {:position :relative}
   [:p
    {:display :inline-block
     :padding-left :17px}]
   [:.blue-dot
    {:position :absolute
     :top :4px
     }]])

(defn- format-poll-str
  [poll-name poll]
  (let [total (get poll :total "x")
        last-week (get poll :lastWeek "x")
        last-month (get poll :lastMonth "x")]
    (gstring/format "%s(t:%s,w:%s,m:%s)" poll-name total last-week last-month)))

(defn unit
  "pomelo-metadata-banner"
  []
  (let [request-id (util/id-atom)]
    (pomelo-metadata-fetch/request @request-id)
    (fn []
      (let [request-ctx @(d-request-ctx/fetch @request-id)
            pending (:pending request-ctx)
            is-success (:is-success request-ctx)
            error-message (string/lower-case (get-in request-ctx [:error :message] ""))
            error-message (when-not (empty? error-message) (str ": " error-message))
            pomelo-metadata @(d-pomelo-metadata/fetch)
            identity-poll (get pomelo-metadata :identityPoll)
            identity-poll-str (when identity-poll (format-poll-str "Identity" identity-poll))
            participant-poll (get pomelo-metadata :participantPoll)
            participant-poll-str (when identity-poll
                                   (format-poll-str "Participant" participant-poll))
            project-poll (get pomelo-metadata :projectPoll)
            project-poll-str (when identity-poll
                               (format-poll-str "Project" project-poll))
            banner-content (into [:p]
                                 [[:span identity-poll-str]
                                  [:span participant-poll-str]
                                  [:span project-poll-str]])]
        [s/box {:class (css-pomelo-metadata-banner)}
         (if (and (not pomelo-metadata) pending)
           [:p "loading banner..."]
           (if pomelo-metadata
             [:div [blue-dot/unit {:data pomelo-metadata}] banner-content]
             [:p "failed to load banner" error-message]))]))))

