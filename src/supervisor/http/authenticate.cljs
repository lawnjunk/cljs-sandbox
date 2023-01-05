(ns supervisor.http.authenticate)

(defn api-fx
  "v1/supervisor/authenticate

  gerate the spec for a :api fx to authenticate a supervisor account
  [payload]
    :request-id
    :email
    :password "
  [payload]
  (let [request-id (get payload :request-id)
        payload (dissoc payload :request-id)]
    {:request-id request-id
     :route "/v1/supervisor/authenticate"
     :req-data payload
     :fx [[:dispatch [:handle-api-v1-supervisor-authenticate]]]
     }))
