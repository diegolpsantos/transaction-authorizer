(ns database.in-memory-account-repository
  (:require [application.repositories.account-repository :as repo]))

(defn- generate-uuid []
  (-> (random-uuid)
      (str)))

(defrecord In-memory-account-repository [state]
  repo/AccountRepository

  (create [_ account]
    (let [account-with-id (assoc account :id (generate-uuid))]
      (swap! state conj account-with-id)
      account-with-id))

  (get-by-id [_ id]
    (first (filter #(= id (:id %))
                   @state)))
  
  (get-by-document [_ document]
             (first (filter #(= document (:document %))
                            @state))))