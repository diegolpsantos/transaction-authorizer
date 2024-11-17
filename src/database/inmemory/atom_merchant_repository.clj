(ns database.inmemory.atom-merchant-repository
  (:require [application.repositories.merchant-repository :as repo]))

(defrecord atom-merchant-repository [state]
  repo/MerchantRepository

  (get-by-name [_ name]
    (first (filter #(= name (:name %))
                   @state))))