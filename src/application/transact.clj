(ns application.transact 
  (:require
   [domain.balance :as balance]
   [domain.balance-repository :as balance-repo]
   [domain.account-repository :as account-repo]))

(defn- authorize [{:keys [mcc total-amount account-id] :as transaction} {:keys [balance-repo]}]
  (let [account-balances (balance-repo/get-by-account-id balance-repo account-id)
        valid-balances (balance/get-balances mcc account-balances)]
    (if (balance/has-balance? total-amount valid-balances)
      (do (let [new-balances (balance/decrease transaction valid-balances)] 
            (balance-repo/update! balance-repo new-balances))
          {:code "00"})
      {:code "51"})))

(defn execute [{:keys [account-id] :as transaction} {:keys [account-repo] :as deps}]
  (try
    (let [user (account-repo/get-by-id account-repo account-id)]
      (if (empty? user)
        {:code "07"}
        (authorize transaction deps))) 
    (catch Exception ex
      (.printStackTrace ex)
      {:code "07"})))