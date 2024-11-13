(ns application.repositories.account-repository)

(defprotocol AccountRepository
  (create [this account])
  (get-by-id [this id])
  (get-by-document [this document]))