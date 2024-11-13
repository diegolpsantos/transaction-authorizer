(ns infra.http.routes.account
  (:require [infra.http.routes.middleware :as middleware]
            [application.create-account :as create-account]))

(defn- create [{:keys [body-params deps]}]
  {:status 201
   :body (create-account/execute body-params deps)})

(defn route [deps]
  ["/priv/accounts"
   {:middleware [(middleware/wrap-dependencies deps)]
    :post {:summary "Create an account"
           :parameters {:body [:map
                               [:first-name string?]
                               [:last-name string?]
                               [:document string?]
                               [:age int?]]}
           :responses {201 {:body [:map [:id string?]]}}
           :handler create}}])