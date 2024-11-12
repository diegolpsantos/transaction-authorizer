(ns transaction-authorizer.system
  (:require [application.http.router :as http-router]
            [application.http.http-server :as http-server]
            [infra.http.routes.transaction :as transaction-router]
            [infra.http.reitit-adapter :as reitit-adapter]
            [infra.http.ring-adapter :as ring-adapter]
            [database.in-memory-account-repository :as account-repository]
            [database.in-memory-merchant-repository :as merchant-repository]
            [database.in-memory-balance-repository :as balance-repository]))

(def ^:private profiles #{:prod :homolog :dev :test})

(def merchant-name "UBER EATS                   SAO PAULO BR")

(def merchants (atom [{:name merchant-name :mcc "5411"}]))

(def users (atom [{:id "123" :first-name "Diego" :last-name "Santos" :age 39}]))

(def balances [{:id "1" :type "food" :code ["5411" "5412"] :total-amount 3000.00 :account-id "123"}
               {:id "2" :type "meal" :code ["5811" "5812"] :total-amount 2000.00 :account-id "123"}
               {:id "3" :type "cash" :code ["1111"] :total-amount 5000.00 :account-id "123"}])

(defn- profile [environment]
  (let [env (keyword environment)]
    (get profiles env :test)))

(defn- repositories [env]
  (let [profile (profile env)]
    (cond
      (= profile :test)
      {:account-repo  (account-repository/->In-memory-account-repository users)
       :merchant-repo (merchant-repository/->In-memory-merchant-repository merchants)
       :balance-repo  (balance-repository/->In-memory-balance-repo balances)}

      :else
      {:account-repo  (account-repository/->In-memory-account-repository users)
       :merchant-repo (merchant-repository/->In-memory-merchant-repository merchants)
       :balance-repo  (balance-repository/->In-memory-balance-repo balances)})))

(defn config [env]
  (let [deps (repositories env)
        routes                                  (-> []
                                                    #_(into [(account-router/route account-repo)])
                                                    (into [(transaction-router/route deps)]))
        server-adapter                          (->> routes
                                                     reitit-adapter/->reitit-adapter
                                                     http-router/create
                                                     ring-adapter/->ring-adapter)]
    (http-server/listen server-adapter {:port  8080
                                        :join? false})))