(ns application.usecases.accounts.create-account
  (:require [application.repositories.account-repository :as account-repository]
            [application.repositories.balance-repository :as balance-repository]))

(defn execute [{:keys [document] :as account} {:keys [account-repo balance-repo]}]
  (let [existent-account (account-repository/get-by-document account-repo document)]
    (if (or (nil? existent-account)
            (= {} existent-account))
      (let [{:keys [id] :as inserted-account} (account-repository/create account-repo account)]
        (balance-repository/create balance-repo id)
        inserted-account)
      existent-account)))