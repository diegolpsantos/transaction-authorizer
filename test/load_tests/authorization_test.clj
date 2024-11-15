(ns load-tests.authorization-test
  (:require [clj-gatling.core :as clj-gatling]
            [clj-http.client :as http]
            [cheshire.core :as json]))

(defn- generate-uuid []
  (-> (random-uuid)
      (str)))

(defn input []
  {:id (generate-uuid)
   :account-id   "123"
   :total-amount 15000.00
   :mcc          "5811"
   :merchant     "PADARIA DO ZE               SAO PAULO BR"})

(defn- option [body]
  {:body             (json/encode body)
   :content-type     :json
   :accept           :json
   :throw-exceptions false})

(defn localhost-request [_]
  (let [{:keys [status body]} (http/post "http://localhost:8080/api/v1/transactions" (option (input))) 
        ch-boy (json/parse-string body true)]
    (and (= status 200)
         (= ch-boy {:code "00"}))))

(defn run-simulation []
  (clj-gatling/run
   {:name "Simulation"
    :scenarios [{:name "Localhost test scenario"
                 :steps [{:name "Root"
                          :request localhost-request}]}]}
   {:concurrency 10
    :timeout-in-ms 1000}))
