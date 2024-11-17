(ns database.sql.postgres-connection-adapter
   (:require [application.database.connection :as conn] 
             [camel-snake-kebab.core :as csk]
             [camel-snake-kebab.extras :as cske] 
             [clj-postgresql.types]
             [clj-postgresql.core :as pg]
             [clojure.java.jdbc :as jdbc]))

 (defn get-pool [{:keys [host dbname user password]}] 
   (pg/pool :host host
            :user user
            :dbname dbname
            :password password))

 (defn- parse-in [value]
   (cske/transform-keys csk/->snake_case value))

  (defn- parse-out [value]
    (let [unamespaced-value (update-keys value (comp keyword name))]
      (cske/transform-keys csk/->kebab-case unamespaced-value)))
 
 (defrecord postgres-connection-adapter [db-info]
   conn/connection

   (find-by-keys [_ table keys]
     (let [converted-keys (parse-in keys)
           data-source (get-pool db-info)
           result (->> converted-keys
                       (jdbc/find-by-keys data-source table)
                       (map parse-out))]
       (pg/close! data-source)
       result))

   (find-by-id [_ table id]
     (let [data-source (get-pool db-info)
           result (->> id
                       (jdbc/get-by-id data-source table)
                       parse-out)]
       (pg/close! data-source)
       result))

   (insert! [_ table value]
     (let [converted_value (parse-in value)
           data-source (get-pool db-info)
           result (-> data-source
                      (jdbc/insert!
                       table
                       converted_value
                       {:suffix "RETURNING *"})
                      first
                      parse-out)]
       (pg/close! data-source)
       result))

   (update! [_ table {:keys [id] :as new-value}]
     (let [value (parse-in new-value)
           data-source (get-pool db-info)
           result (jdbc/update! data-source table value ["id = ?" id])]
       (pg/close! data-source)
       result)))

(comment
  (def cp (->postgres-connection-adapter {:host "localhost"
                                          :dbname "transaction_authorizer"
                                          :user "postgres"
                                          :password "postgres"})) 

  (def default-input {:id "3a93bf01-ee92-4db4-8df4-b5832b9edfbc"
                      :first-name "Francisca"
                      :last-name "Alves"
                      :age 39
                      :document "55989865075"})

  (conn/find-by-id cp :accounts
                   "1a93bf01-ee92-4db4-8df4-b5832b9edfcb")

  (conn/insert! cp
                :accounts
                default-input)

  (conn/insert! cp
                :balances
                {:id "2a93bf01-ee92-4db4-8df4-b5832b9edf10"
                 :type "food"
                 :code ["5411" "5412"]
                 :total-amount 0.00
                 :account-id "1a93bf01-ee92-4db4-8df4-b5832b9edfcb"})
  
  (conn/find-by-keys cp :balances {:account-id "1a93bf01-ee92-4db4-8df4-b5832b9edfcb"})

  (conn/update! cp :balances {:id "2a93bf01-ee92-4db4-8df4-b5832b9edf10" :total-amount 40.00})

  )
