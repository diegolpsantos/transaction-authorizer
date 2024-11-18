(ns database.sql.sql-account-repository
  (:require [application.repositories.account-repository :as repo]
            [application.database.connection :as conn]
            [database.uuid-generator :as id-generator]))

(defrecord sql-account-repository [connection]
  repo/AccountRepository

  (create [_ account]
    (let [account-with-id (assoc account :id (id-generator/generate))
          created-account (conn/insert! connection :accounts account-with-id)]
      created-account))

  (get-by-id [_ id]
    (conn/find-by-id connection :accounts id))

  (get-by-document [_ document]
    (first (conn/find-by-keys connection :accounts {:document document}))))