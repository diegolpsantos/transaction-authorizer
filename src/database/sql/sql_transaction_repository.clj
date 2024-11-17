(ns database.sql.sql-transaction-repository
  (:require [application.repositories.transaction-repository :as transaction-repo]
            [application.database.connection :as conn]))

(defrecord sql-transaction-repository [connection]
  transaction-repo/transaction-repository
  
  (create! [_ transaction]
    (conn/insert! connection :transactions transaction))
  
  (get-by-id [_ id]
    (conn/find-by-id connection :transactions id)))