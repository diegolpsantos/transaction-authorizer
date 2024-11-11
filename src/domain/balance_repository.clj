(ns domain.balance-repository)

(defprotocol BalanceRepository
  (update! [this balance])
  (get-by-account-id [this id]))