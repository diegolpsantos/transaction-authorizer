(ns database.in-memory-account-repository
  (:require [domain.account-repository :as repo]))

(defrecord In-memory-account-repository [state]
  repo/AccountRepository
  
  (get-by-id [_ id]
    (first (filter #(= id (:id %))
                   @state))))