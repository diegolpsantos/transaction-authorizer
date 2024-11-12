(ns infra.http.routes.transaction-test
  (:require [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test :refer [match?]]
            [muuntaja.core :as m]
            [application.http.router :as router-port]
            [infra.http.reitit-adapter :as router-adapter]
            [database.in-memory-account-repository :as account-repository]
            [database.in-memory-merchant-repository :as merchant-repository]
            [database.in-memory-balance-repository :as balance-repository]
            [infra.http.routes.transaction :as transaction]))

(def transactions-path "/api/v1/transactions")

(def merchant-name "UBER EATS                   SAO PAULO BR")

(def merchants (atom [{:name merchant-name :mcc "5411"}]))

(def users (atom [{:id "123" :first-name "Diego" :last-name "Santos" :age 39}]))

(def balances [{:id "1" :type "food" :code ["5411" "5412"] :total-amount 3000.00 :account-id "123"}
               {:id "2" :type "meal" :code ["5811" "5812"] :total-amount 2000.00 :account-id "123"}
               {:id "3" :type "cash" :code ["1111"] :total-amount 5000.00 :account-id "123"}])

(def deps {:account-repo  (account-repository/->In-memory-account-repository users)
           :merchant-repo (merchant-repository/->In-memory-merchant-repository merchants)
           :balance-repo  (balance-repository/->In-memory-balance-repo balances)})

(defn route []
  (-> deps
      transaction/route
      router-adapter/->reitit-adapter
      router-port/create))

(deftest transaction-handler
  (testing "should create a route to authorize transactions"
    (let [route    (route)
          response (route {:request-method :post
                           :uri            transactions-path
                           :headers        {"content-type" "application/edn"
                                            "accept"       "application/transit+json"}
                           :body-params    {:account-id   "123"
                                            :total-amount 100.00
                                            :mcc          "5811"
                                            :merchant     "PADARIA DO ZE               SAO PAULO BR"}})]
      (is (match? {:status 200} response))
      (is (match? {:code "00"}
                  (m/decode-response-body response)))))
  
  (testing "should return bad request when data is invalid"
    (let [route    (route)
          response (route {:request-method :post
                           :uri            transactions-path
                           :headers        {"content-type" "application/edn"
                                            "accept"       "application/transit+json"}
                           :body-params    {:amount 10.0}})]
      (is (match? {:status 400} response))))
  
  (testing "should return Method Not Allowed when request method is invalid"
    (let [route    (route)
          response (route {:request-method :delete
                           :uri            transactions-path
                           :headers        {"content-type" "application/edn"
                                            "accept"       "application/transit+json"}
                           :body-params    {:amount 10.0}})]
      (is (match? {:status 405} response)))))