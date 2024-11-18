(ns transaction-authorizer.core
  "FIXME: my new org.corfield.new/scratch project."
  (:require [transaction-authorizer.system :as stm]))

(defonce service nil)

(defn -main
  "Invoke me with clojure -M -m transaction-authorizer.core"
  [& args]
  (let [env (first args)]
    (->> env
         stm/config
         constantly
         (alter-var-root #'service))))

(defn -close []
  (.stop service))