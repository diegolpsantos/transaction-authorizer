(ns database.inmemory.atom-balance-repository
  (:require [application.repositories.balance-repository :as repo]
            [database.uuid-generator :as id-generator]))

(defrecord atom-balance-repo [state]
  repo/BalanceRepository
  
  (create [_ account-id]
    (let [food-id (id-generator/generate)
          meal-id (id-generator/generate)
          cash-id (id-generator/generate)]
      (alter state
             conj
             {food-id {:id food-id :type "food" :code ["5411" "5412"] :total-amount 0.00 :account-id account-id}}
             {meal-id {:id meal-id :type "meal" :code ["5811" "5812"] :total-amount 0.00 :account-id account-id}}
             {cash-id {:id cash-id :type "cash" :code ["1111"] :total-amount 0.00 :account-id account-id}})))

  (update! [_ {:keys [id total-amount]}]
    (alter state update id (fn [v] (update v :total-amount (fn [_]
                                                             total-amount)))))
  
  (get-by-account-id [_ id]
    (filter #(= id (:account-id %))
            (vals @state))))
