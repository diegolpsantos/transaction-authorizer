(ns application.transact-test
  (:require [clojure.test :refer [deftest is testing]]
            [application.transact :as transact]
            [database.in-memory-balance-repository :as balance-repo]
            [database.in-memory-account-repository :as account-repo]
            [database.in-memory-merchant-repository :as merchant-repo]))

(def merchant-name "UBER EATS                   SAO PAULO BR")

(def merchants (atom [{:name merchant-name :mcc "5411"}]))

(def users (atom [{:id "123" :first-name "Diego" :last-name "Santos" :age 39}]))

(def balances [{:id "1" :type "food" :code ["5411" "5412"] :total-amount 3000.00 :account-id "123"}
               {:id "2" :type "meal" :code ["5811" "5812"] :total-amount 2000.00 :account-id "123"}
               {:id "3" :type "cash" :code ["1111"] :total-amount 5000.00 :account-id "123"}])

(defn deps [] 
  {:balance-repo  (balance-repo/->In-memory-balance-repo balances)
   :account-repo  (account-repo/->In-memory-account-repository users)
   :merchant-repo (merchant-repo/->In-memory-merchant-repository merchants)})

(deftest execute
  (let [deps (deps)]
    (testing "should approve a transaction using mcc balance"
      (let [payload {:account-id "123" :total-amount 100.00 :mcc "5811" :merchant "PADARIA DO ZE               SAO PAULO BR"}]
        (is (= {:code "00"}
               (transact/execute payload deps)))))

    (testing "should approve a transaction using mcc and cash balance"
      (let [payload {:account-id "123" :total-amount 4000.00 :mcc "5811" :merchant "PADARIA DO ZE               SAO PAULO BR"}]
        (is (= {:code "00"}
               (transact/execute payload deps)))))

    (testing "should approve using the name of the merchant to discover the balance"
      (let [payload {:account-id "123" :total-amount 3000.00 :mcc "5811" :merchant merchant-name}]
        (is (= {:code "00"}
               (transact/execute payload deps)))))

    (testing "should not approve a transaction when insuficient funds"
      (let [payload {:account-id "123" :total-amount 10000.00 :mcc "5811" :merchant "PADARIA DO ZE               SAO PAULO BR"}]
        (is (= {:code "51"}
               (transact/execute payload deps)))))

    (testing "should not approve a transaction when user not found"
      (let [payload {:account-id "1234" :total-amount 100.00 :mcc "5811" :merchant "PADARIA DO ZE               SAO PAULO BR"}]
        (is (= {:code "07"}
               (transact/execute payload deps)))))))
