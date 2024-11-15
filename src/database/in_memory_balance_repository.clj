(ns database.in-memory-balance-repository
  (:require [application.repositories.balance-repository :as repo]))

(defn- generate-uuid []
  (-> (random-uuid)
      (str)))

(defrecord In-memory-balance-repo [state]
  repo/BalanceRepository
  
  (create [_ account-id]
    (let [food-id (generate-uuid)
          meal-id (generate-uuid)
          cash-id (generate-uuid)]
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
