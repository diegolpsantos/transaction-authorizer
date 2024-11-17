#!/usr/bin/env bb

(require '[clojure.java.shell :as sh])

(defn sql-docker [command]
  (sh/sh "docker" "exec" "some-postgres" "psql" "-U" "postgres" "-d" "transaction_authorizer" "-c"  command))

(defn create-database []
  (sh/sh "docker" "exec" "some-postgres" "psql" "-U" "postgres" "-c" "CREATE DATABASE transaction_authorizer"))

;; COMMAND TO TRY AFTER:
;; psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = '<your db name>'" | grep -q 1 | psql -U postgres -c "CREATE DATABASE <your db name>"

(create-database)