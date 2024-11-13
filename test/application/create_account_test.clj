(ns application.create-account-test
  (:require [clojure.test :refer [deftest is testing]]
            [matcher-combinators.test :refer [match?]]
            [application.create-account :as create-account]
            [database.in-memory-balance-repository :as balance-repo]
            [database.in-memory-account-repository :as account-repo]))

(defn- build-account-data []
  (atom [{:id "123" :first-name "Diego" :last-name "Santos" :age 39 :document "10203040"}]))

(defn deps []
  {:balance-repo  (balance-repo/->In-memory-balance-repo [])
   :account-repo  (account-repo/->In-memory-account-repository (build-account-data))})

(deftest execute
  (testing "should create an account"
    (is (match? {:id string?}
                (create-account/execute {:last-name "Santos", :age 20, :first-name "Diego", :document "12345"} (deps)))))
  (testing "should not create an account with a existent document"
    (is (match? {:id "123"}
                (create-account/execute {:last-name "Santos", :age 20, :first-name "Diego", :document "10203040"} (deps))))))