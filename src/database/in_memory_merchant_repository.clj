(ns database.in-memory-merchant-repository
  (:require [domain.merchant-repository :as repo]))

(defrecord In-memory-merchant-repository [state]
  repo/MerchantRepository
  
  (get-by-name [_ name]
    (first (filter #(= name (:name %))
                 @state))))