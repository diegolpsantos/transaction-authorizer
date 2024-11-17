(ns transaction-authorizer.system
  (:require [application.http.router :as http-router]
            [application.http.http-server :as http-server]
            [infra.http.routes.transaction :as transaction-router]
            [infra.http.routes.account :as account-router]
            [infra.http.reitit-adapter :as reitit-adapter]
            [infra.http.ring-adapter :as ring-adapter]
            [transaction-authorizer.test-dependencies :as test-deps]
            [transaction-authorizer.prod-dependencies :as prod-deps]))

(def ^:private profiles #{:prod :homolog :dev :test})

(defn- profile [environment]
  (let [env (keyword environment)]
    (get profiles env :test)))

(defn- repositories [env]
  (let [profile (profile env)]
    (cond
      (= profile :test)
      (test-deps/dependencies)

      :else
      (prod-deps/dependencies))))

(defn config [env]
  (let [deps (repositories env)
        routes                                  (-> []
                                                    (into [(account-router/route deps)])
                                                    (into [(transaction-router/route deps)]))
        server-adapter                          (->> routes
                                                     reitit-adapter/->reitit-adapter
                                                     http-router/create
                                                     ring-adapter/->ring-adapter)]
    (http-server/listen server-adapter {:port  8080
                                        :join? false})))