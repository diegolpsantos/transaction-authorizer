(ns application.repositories.transaction-repository)

(defprotocol transaction-repository
  (create! [this transaction])
  (get-by-id [this id]))