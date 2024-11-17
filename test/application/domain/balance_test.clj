(ns application.domain.balance-test
  (:require [clojure.test :refer [deftest is testing]]
            [application.domain.balance :as balance]))

(deftest has-balance?
  (testing "should return true when has funds"
    (is (true? (balance/has-balance? 1000.00 [{:id "2" :type "meal" :code ["5811" "5812"] :total-amount 2000.00 :account-id "123"}
                                              {:id "3" :type "cash" :code ["1111"] :total-amount 5000.00 :account-id "123"}]))))

  (testing "should return true when has funds in meal and cash"
    (is (true? (balance/has-balance? 7000.00 [{:id "2" :type "meal" :code ["5811" "5812"] :total-amount 2000.00 :account-id "123"}
                                              {:id "3" :type "cash" :code ["1111"] :total-amount 5000.00 :account-id "123"}]))))

  (testing "should return false when does not have funds"
    (is (false? (balance/has-balance? 8000.00 [])))
    (is (false? (balance/has-balance? 8000.00 nil)))
    (is (false? (balance/has-balance? 8000.00 [{:id "2" :type "meal" :code ["5811" "5812"] :total-amount 2000.00 :account-id "123"}
                                               {:id "3" :type "cash" :code ["1111"] :total-amount 0.00 :account-id "123"}])))))

(deftest decrease
  (testing "should update the balance"
    (let [input {:account-id "123" :total-amount 100.00 :mcc "5811" :merchant "PADARIA DO ZE               SAO PAULO BR"}
          balance [{:id "2" :type "meal" :code ["5811" "5812"] :total-amount 2000.00 :account-id "123"}
                   {:id "3" :type "cash" :code ["1111"] :total-amount 0.00 :account-id "123"}]]
      (is (= [{:balance {:id "2", :type "meal", :code ["5811" "5812"], :total-amount 1900.0, :account-id "123"}, :rest 0}]
             (balance/decrease input
                               balance)))))

  (testing "should update the mcc balance and cash balance"
    (let [input {:account-id "123" :total-amount 5000.00 :mcc "5811" :merchant "PADARIA DO ZE               SAO PAULO BR"}
          balance [{:id "2" :type "meal" :code ["5811" "5812"] :total-amount 2000.00 :account-id "123"}
                   {:id "3" :type "cash" :code ["1111"] :total-amount 5000.00 :account-id "123"}]]
      (is (= [{:balance {:id "2", :type "meal", :code ["5811" "5812"], :total-amount 0.0, :account-id "123"}, :rest 3000.0}
              {:balance {:id "3", :type "cash", :code ["1111"], :total-amount 2000.0, :account-id "123"}}]
             (balance/decrease input
                               balance))))))
