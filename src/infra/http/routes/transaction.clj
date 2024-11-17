(ns infra.http.routes.transaction
  (:require [application.usecases.authorizations.transact :as transact]
            [infra.http.routes.middleware :as middleware]))

(defn- transaction-handler [{:keys [body-params deps]}]
  {:status 200
   :body (transact/execute body-params deps)})

(defn route [deps]
  ["/transactions"
   {:middleware [(middleware/wrap-dependencies deps)]
    :post {:summary "Authorize a transaction"
           :parameters {:body [:map
                               [:id string?]
                               [:account-id string?]
                               [:total-amount double?]
                               [:mcc string?]
                               [:merchant string?]]}
           :responses {200 {:body [:map
                                   [:code [:enum "00" "51" "07"]]]}}
           :handler transaction-handler}}])
