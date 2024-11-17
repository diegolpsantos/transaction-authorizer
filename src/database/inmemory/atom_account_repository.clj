(ns database.inmemory.atom-account-repository
  (:require [application.repositories.account-repository :as repo]
            [database.uuid-generator :as id-gen]))

(defrecord atom-account-repository [state]
  repo/AccountRepository

  (create [_ account]
    (let [account-with-id (assoc account :id (id-gen/generate))]
      (swap! state conj account-with-id)
      account-with-id))

  (get-by-id [_ id]
    (first (filter #(= id (:id %))
                   @state)))
  
  (get-by-document [_ document]
             (first (filter #(= document (:document %))
                            @state))))