(ns database.in-memory-balance-repository
  (:require [domain.repositories.balance-repository :as repo]))

(defrecord In-memory-balance-repo [state]
  repo/BalanceRepository
  
  (update! [_ balance]
    (conj balance))
  
  (get-by-account-id [_ id]
    (filter #(= id (:account-id %))
            state)))