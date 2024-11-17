(ns database.sql.sql-account-repository
  (:require [application.repositories.account-repository :as repo]
            [application.database.connection :as conn]))

(defrecord sql-account-repository [connection]
  repo/AccountRepository
  
  (create [_ account]
    (let [created-account (conn/insert! connection :accounts account)]
      created-account))
  
  (get-by-id [_ id]
    (conn/find-by-id connection :accounts {:id id}))

  (get-by-document [_ document]
    (conn/find-by-keys connection :accounts {:document document})))