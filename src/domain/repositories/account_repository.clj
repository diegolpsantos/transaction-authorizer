(ns domain.repositories.account-repository)

(defprotocol AccountRepository 
  (get-by-id [this id]))