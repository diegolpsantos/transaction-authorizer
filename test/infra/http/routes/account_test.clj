(ns infra.http.routes.account-test
  (:require [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test :refer [match?]]
            [muuntaja.core :as m]
            [application.http.router :as router-port]
            [infra.http.reitit-adapter :as router-adapter]
            [database.in-memory-account-repository :as account-repository]
            [database.in-memory-balance-repository :as balance-repository]
            [infra.http.routes.account :as accounts]))

(def account-path "/api/v1/priv/accounts")

(defn- build-account-data []
  (atom [{:id "123" :first-name "Diego" :last-name "Santos" :age 39 :document "10203040"}]))

(def deps
  {:balance-repo  (balance-repository/->In-memory-balance-repo (ref {}))
   :account-repo  (account-repository/->In-memory-account-repository (build-account-data))})

(defn route []
  (-> deps
      accounts/route
      router-adapter/->reitit-adapter
      router-port/create))

(deftest create
  (testing "Context of the test assertions"
    (let [route    (route)
          response (route {:request-method :post
                           :uri            account-path
                           :headers        {"content-type" "application/json"
                                            "accept"       "application/transit+json"}
                           :body-params    {:last-name "Santos"
                                            :age 20
                                            :first-name "Diego"
                                            :document "12345"}})]
      (is (match? {:status 201} response))
      (is (match? {:id string?}
                  (m/decode-response-body response))))))