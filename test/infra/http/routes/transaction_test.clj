(ns infra.http.routes.transaction-test
  (:require [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test :refer [match?]]
            [muuntaja.core :as m]
            [application.http.router :as router-port]
            [infra.http.reitit-adapter :as router-adapter]
            [database.inmemory.atom-account-repository :as account-repository]
            [database.inmemory.atom-merchant-repository :as merchant-repository]
            [database.inmemory.atom-balance-repository :as balance-repository]
            [database.inmemory.atom-transaction-repository :as transaction-repo]
            [infra.http.routes.transaction :as transaction]))

(def transactions-path "/api/v1/transactions")

(def merchant-name "UBER EATS                   SAO PAULO BR")

(def merchants (atom [{:name merchant-name :mcc "5411"}]))

(def users (atom [{:id "123" :first-name "Diego" :last-name "Santos" :age 39}]))

(def balances (ref {"1" {:id "1" :type "food" :code ["5411" "5412"] :total-amount 3000.00 :account-id "123"}
                    "2" {:id "2" :type "meal" :code ["5811" "5812"] :total-amount 2000.00 :account-id "123"}
                    "3" {:id "3" :type "cash" :code ["1111"] :total-amount 5000.00 :account-id "123"}}))

(def transactions (atom [{:id "af5ea0ce-795c-41ee-b060-0c8788de79b5"
                          :account-id "123"
                          :total-amount 10000.00
                          :mcc "5811"
                          :merchant "PADARIA DO ZE               SAO PAULO BR"}]))

(def deps {:account-repo     (account-repository/->atom-account-repository users)
           :merchant-repo    (merchant-repository/->atom-merchant-repository merchants)
           :balance-repo     (balance-repository/->atom-balance-repo balances)
           :transaction-repo (transaction-repo/->atom-transaction-repository transactions)})

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
                           :body-params    {:id "a20b09b6-d94d-4548-b4df-0afa0ee96eb9"
                                            :account-id   "123"
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