(ns transaction-authorizer.test-dependencies
  (:require [database.inmemory.atom-account-repository :as account-repository]
            [database.inmemory.atom-merchant-repository :as merchant-repository]
            [database.inmemory.atom-balance-repository :as balance-repository]
            [database.inmemory.atom-transaction-repository :as transaction-repository]))

(def ^:private merchant-name "UBER EATS                   SAO PAULO BR")

(def ^:private merchants (atom [{:name merchant-name :mcc "5411"}]))

(def ^:private users (atom [{:id "123" :first-name "Diego" :last-name "Santos" :age 39 :document "10203040"}]))

(def ^:private balances (ref {"1" {:id "1" :type "food" :code ["5411" "5412"] :total-amount 3000.00 :account-id "123"}
                              "2" {:id "2" :type "meal" :code ["5811" "5812"] :total-amount 10000.00 :account-id "123"}
                              "3" {:id "3" :type "cash" :code ["1111"] :total-amount 5000.00 :account-id "123"}}))

(def ^:private transactions (atom [{:id "af5ea0ce-795c-41ee-b060-0c8788de79b5"
                                    :account-id "123"
                                    :total-amount 10000.00
                                    :mcc "5811"
                                    :merchant "PADARIA DO ZE               SAO PAULO BR"}]))

(defn dependencies []
  {:account-repo  (account-repository/->atom-account-repository users)
   :merchant-repo (merchant-repository/->atom-merchant-repository merchants)
   :balance-repo  (balance-repository/->atom-balance-repo balances)
   :transaction-repo (transaction-repository/->atom-transaction-repository transactions)})
