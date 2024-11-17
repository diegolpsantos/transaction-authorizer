(ns database.sql.sql-balance-repository
  (:require [application.repositories.balance-repository :as repo]
            [application.database.connection :as conn]
            [database.uuid-generator :as id-generator]
            [database.sql.postgres-connection-adapter :as conn-adapter]))

(defrecord sql-balance-repository [connection]
  repo/BalanceRepository

  (create [_ account-id]
    (conn/insert! connection :balances {:id (id-generator/generate) :type "food" :code ["5411" "5412"] :total-amount 0.00 :account-id account-id})
    (conn/insert! connection :balances {:id (id-generator/generate) :type "meal" :code ["5811" "5812"] :total-amount 0.00 :account-id account-id})
    (conn/insert! connection :balances {:id (id-generator/generate) :type "cash" :code ["1111"] :total-amount 0.00 :account-id account-id}))

  (update! [_ new-value]
    (conn/update! connection :balances new-value))

  (get-by-account-id [_ account-id]
    (conn/find-by-keys connection :balances {:account-id account-id})))

(comment
  (def db-config
    {:dbtype "postgresql"
     :jdbcUrl "jdbc:postgresql://localhost:5432/transaction_authorizer"
     :user "postgres"
     :password "postgres"})

  (def cp (conn-adapter/->postgres-connection-adapter db-config))

  (def a (->sql-balance-repository cp))

  (repo/get-by-account-id a "1a93bf01-ee92-4db4-8df4-b5832b9edfcb")

  (repo/create a "1a93bf01-ee92-4db4-8df4-b5832b9edfcb"))