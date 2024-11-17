(ns database.sql.sql-merchant-repository
  (:require [application.repositories.merchant-repository :as merchant-repo]
            [application.database.connection :as conn]))

(defrecord sql-merchant-repository [connection]
  merchant-repo/MerchantRepository
  
  (get-by-name [_ name]
    (conn/find-by-keys connection :merchants {:name name})))