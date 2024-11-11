(ns domain.account-repository)

(defprotocol AccountRepository 
  (get-by-id [this id]))