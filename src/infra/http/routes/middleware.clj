(ns infra.http.routes.middleware)

(defn wrap-dependencies [deps]
  {:name ::wrap-dependencies
   :wrap (fn [handler]
           (fn [request]
             (let [new-request (assoc request :deps deps)]
               (handler new-request))))})