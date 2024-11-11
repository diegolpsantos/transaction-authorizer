(ns application.transact 
  (:require
   [domain.balance :as balance]
   [domain.repositories.balance-repository :as balance-repo]
   [domain.repositories.account-repository :as account-repo]
   [domain.repositories.merchant-repository :as merchant-repo]))

(defn- choose-mcc [repo merchant-name mcc]
  (let [merchant     (merchant-repo/get-by-name repo merchant-name)
        merchant-mcc (get merchant :mcc)]
    (cond 
      (nil? merchant) mcc
      (not= merchant-mcc mcc) merchant-mcc
      :else mcc)))

(defn- authorize [{:keys [mcc total-amount merchant account-id] :as transaction} {:keys [balance-repo merchant-repo]}]
  (let [account-balances (balance-repo/get-by-account-id balance-repo account-id) 
        chosen-mcc       (choose-mcc merchant-repo merchant mcc)
        txn              (assoc transaction :mcc chosen-mcc)
        valid-balances   (balance/get-balances chosen-mcc account-balances)]
    (if (balance/has-balance? total-amount valid-balances)
      (do (let [updated-balances (balance/decrease txn valid-balances)]
            (balance-repo/update! balance-repo updated-balances))
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
