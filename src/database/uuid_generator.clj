(ns database.uuid-generator)

(defn generate []
  (-> (random-uuid)
      (str)))