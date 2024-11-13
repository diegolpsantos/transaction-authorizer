(ns database.in-memory-balance-repository
  (:require [application.repositories.balance-repository :as repo]))

(defn- generate-uuid []
  (-> (random-uuid)
      (str)))

(defrecord In-memory-balance-repo [state]
  repo/BalanceRepository
  
  (create [_ account-id] 
    (conj state
          {:id (generate-uuid) :type "food" :code ["5411" "5412"] :total-amount 0.00 :account-id account-id}
          {:id (generate-uuid) :type "meal" :code ["5811" "5812"] :total-amount 0.00 :account-id account-id}
          {:id (generate-uuid) :type "cash" :code ["1111"] :total-amount 0.00 :account-id account-id}))

  (update! [_ balance]
    (conj balance))
  
  (get-by-account-id [_ id]
    (filter #(= id (:account-id %))
            state)))