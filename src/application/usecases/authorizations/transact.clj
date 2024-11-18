(ns application.usecases.authorizations.transact
  (:require
   [application.domain.balance :as balance]
   [application.repositories.balance-repository :as balance-repo]
   [application.repositories.account-repository :as account-repo]
   [application.repositories.merchant-repository :as merchant-repo]
   [application.repositories.transaction-repository :as transaction-repo]
   [application.usecases.authorizations.transact :as transact]))

(def authorized {:code "00"})
(def insufficient-funds {:code "51"})
(def not-authorized {:code "07"})

(defn- is-processed-transaction? [transaction-repo transaction-id]
  (let [txn (transaction-repo/get-by-id transaction-repo transaction-id)]
    (not (empty? txn))))

(defn- choose-mcc [repo merchant-name mcc]
  (let [merchant     (merchant-repo/get-by-name repo merchant-name)
        merchant-mcc (get merchant :mcc)]
    (cond
      (nil? merchant-mcc) mcc
      (not= merchant-mcc mcc) merchant-mcc
      :else mcc)))

(defn- process-balance [transaction balances balance-repo]
  (->> balances
       (balance/decrease transaction)
       (map :balance)
       (map (fn [balance]
              (balance-repo/update! balance-repo balance)))
       (doall)))

(defn- authorize [{:keys [mcc total-amount merchant account-id] :as transaction} {:keys [balance-repo merchant-repo transaction-repo]}]
  (let [account-balances (balance-repo/get-by-account-id balance-repo account-id)
        chosen-mcc       (choose-mcc merchant-repo merchant mcc)
        txn              (assoc transaction :mcc chosen-mcc)
        valid-balances   (balance/get-balances chosen-mcc account-balances)]
    (if (balance/has-balance? total-amount valid-balances)
      (let [processed-balances      (process-balance txn valid-balances balance-repo)
            tx (assoc txn :balances (doall (map #(:id %) processed-balances)))]
        (transaction-repo/create! transaction-repo tx)
        authorized)
      insufficient-funds)))

(defn execute [{:keys [id account-id] :as transaction} {:keys [account-repo transaction-repo] :as deps}]
  (try
    (let [user (account-repo/get-by-id account-repo account-id)]
      (if (or (empty? user)
              (is-processed-transaction? transaction-repo id))
        not-authorized
        (authorize transaction deps)))
    (catch Exception ex
      (.printStackTrace ex)
      not-authorized)))
