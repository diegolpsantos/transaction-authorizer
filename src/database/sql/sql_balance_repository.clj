(ns database.sql.sql-balance-repository
  (:require [application.repositories.balance-repository :as repo]
            [application.database.connection :as conn]
            [database.uuid-generator :as id-generator]))

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
