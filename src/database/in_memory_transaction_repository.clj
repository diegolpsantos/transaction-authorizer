(ns database.in-memory-transaction-repository
  (:require [application.repositories.transaction-repository :as transaction-repo]))

(defrecord In-memory-transaction-repository [state]
  transaction-repo/transaction-repository

  (create! [_ transaction]
    (swap! state conj transaction))
  
  (get-by-id [_ id]
    (first (filter #(= id (:id %))
                   @state))))