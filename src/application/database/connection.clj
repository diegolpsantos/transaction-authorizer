(ns application.database.connection)

(defprotocol connection
  (find-by-keys [this table keys])
  (find-by-id [this table id])
  (insert! [this table value])
  (update! [this table value]))