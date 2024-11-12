(ns application.repositories.account-repository)

(defprotocol AccountRepository 
  (get-by-id [this id]))