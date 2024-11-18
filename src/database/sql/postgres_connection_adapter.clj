(ns database.sql.postgres-connection-adapter
  (:require [application.database.connection :as conn]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [clj-postgresql.types]
            [clj-postgresql.core :as pg]
            [clojure.java.jdbc :as jdbc]
            [java-time.api :as jt]))

(defn- get-pool [{:keys [host dbname user password]}]
  (pg/pool :host host
           :user user
           :dbname dbname
           :password password))

(defn- parse-in [value]
  (cske/transform-keys csk/->snake_case value))

(defn- parse-out [value]
  (let [unamespaced-value (update-keys value (comp keyword name))]
    (cske/transform-keys csk/->kebab-case unamespaced-value)))

(defn- add-datetime [value key]
  (assoc value key (jt/sql-timestamp)))

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
    (let [converted_value (-> value
                              (add-datetime :inserted-at)
                              (add-datetime :updated-at)
                              (parse-in))
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
    (let [value (-> new-value
                    (add-datetime :updated-at)
                    (parse-in))
          data-source (get-pool db-info)]
      (jdbc/update! data-source table value ["id = ?" id])
      (pg/close! data-source)
      new-value)))
