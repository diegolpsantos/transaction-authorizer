(ns infra.http.routes.transaction
  (:require [application.transact :as transact]
            [infra.http.routes.middleware :as middleware]))

(defn transaction-handler [{:keys [body-params deps]}]
  (let [result (transact/execute body-params deps)]
    {:status 200
     :body result}))

(defn route [deps]
  ["/transactions"
     {:middleware [(middleware/wrap-dependencies deps)]
      :post {:summary "Authorize a transaction"
             :parameters {:body [:map
                                 [:account-id string?]
                                 [:total-amount double?]
                                 [:mcc string?]
                                 [:merchant string?]]}
             :responses {200 {:body [:map
                                     [:code string?]]}}
             :handler transaction-handler}}])