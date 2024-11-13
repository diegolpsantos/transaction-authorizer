(ns application.usecases.authorizations.transact 
  (:require
   [application.domain.balance :as balance]
   [application.repositories.balance-repository :as balance-repo]
   [application.repositories.account-repository :as account-repo]
   [application.repositories.merchant-repository :as merchant-repo]
   [application.repositories.transaction-repository :as transaction-repo]))

(defn- choose-mcc [repo merchant-name mcc]
  (let [merchant     (merchant-repo/get-by-name repo merchant-name)
        merchant-mcc (get merchant :mcc)]
    (cond 
      (nil? merchant) mcc
      (not= merchant-mcc mcc) merchant-mcc
      :else mcc)))

(defn- authorize [{:keys [mcc total-amount merchant account-id] :as transaction} {:keys [balance-repo merchant-repo transaction-repo]}] 
  (let [account-balances (balance-repo/get-by-account-id balance-repo account-id) 
        chosen-mcc       (choose-mcc merchant-repo merchant mcc)
        txn              (assoc transaction :mcc chosen-mcc)
        valid-balances   (balance/get-balances chosen-mcc account-balances)]
    (if (balance/has-balance? total-amount valid-balances)
      (do (let [updated-balances (balance/decrease txn valid-balances)]
            (balance-repo/update! balance-repo updated-balances)
            (transaction-repo/create! transaction-repo txn))
          {:code "00"})
      {:code "51"})))

(defn execute [{:keys [id account-id] :as transaction} {:keys [account-repo transaction-repo] :as deps}] 
  (try
    (let [user (account-repo/get-by-id account-repo account-id)
          txn  (transaction-repo/get-by-id transaction-repo id)]
      (if (or (empty? user) 
              (not= nil txn))
        {:code "07"}
        (authorize transaction deps))) 
    (catch Exception ex
      (.printStackTrace ex)
      {:code "07"})))
