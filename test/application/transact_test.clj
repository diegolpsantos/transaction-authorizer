(ns application.transact-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [application.transact :as transact]
            [database.in-memory-balance-repository :as balance-repo]
            [database.in-memory-account-repository :as account-repo]))

(def users (atom [{:id "123" :first-name "Diego" :last-name "Santos" :age 39}]))

(def state (atom [{:id "1" :type "food" :code ["5411" "5412"] :total-amount 1000.00 :account-id "123"}
                  {:id "2" :type "meal" :code ["5811" "5812"] :total-amount 2000.00 :account-id "123"}
                  {:id "3" :type "cash" :code ["1111"] :total-amount 5000.00 :account-id "123"}]))

(defn deps [] 
  {:balance-repo (balance-repo/->In-memory-balance-repo state)
   :account-repo (account-repo/->In-memory-account-repository users)})

(defn teardown []
  (reset! state []))

(defn wrap-setup [f]
  (f)
  (teardown))

(use-fixtures :once wrap-setup)

(deftest execute
  (let [deps (deps)]
      (testing "should approve a transaction"
      (let [payload {:account-id "123" :total-amount 100.00 :mcc "5811" :merchant-name "PADARIA DO ZE               SAO PAULO BR"}]
        (is (= {:code "00"}
               (transact/execute payload deps)))))
    
    (testing "should not approve a transaction when insuficient funds"
      (let [payload {:account-id "123" :total-amount 10000.00 :mcc "5811" :merchant-name "PADARIA DO ZE               SAO PAULO BR"}]
        (is (= {:code "51"}
               (transact/execute payload deps)))))
    
    (testing "should not approve a transaction when user not found"
      (let [payload {:account-id "1234" :total-amount 100.00 :mcc "5811" :merchant-name "PADARIA DO ZE               SAO PAULO BR"}]
        (is (= {:code "07"}
               (transact/execute payload deps)))))))
