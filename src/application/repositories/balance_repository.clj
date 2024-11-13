(ns application.repositories.balance-repository)

(defprotocol BalanceRepository
  (create [this account-id])
  (update! [this balance])
  (get-by-account-id [this id]))