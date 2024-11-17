(ns transaction-authorizer.prod-dependencies
  (:require [database.sql.postgres-connection-adapter :as sql-connection]
            [database.sql.sql-account-repository :as sql-account-repo]
            [database.sql.sql-balance-repository :as sql-balance-repo]
            [database.sql.sql-merchant-repository :as sql-merchant-repo]
            [database.sql.sql-transaction-repository :as sql-transaction-repo]))

 (def ^:private db-config
   {:host "localhost"
    :dbname "transaction_authorizer"
    :user "postgres"
    :password "postgres"})

(def ^:private connection (sql-connection/->postgres-connection-adapter db-config))
 
 (defn dependencies []
   {:account-repo  (sql-account-repo/->sql-account-repository connection)
    :merchant-repo (sql-merchant-repo/->sql-merchant-repository connection)
    :balance-repo  (sql-balance-repo/->sql-balance-repository connection)
    :transaction-repo (sql-transaction-repo/->sql-transaction-repository connection)})