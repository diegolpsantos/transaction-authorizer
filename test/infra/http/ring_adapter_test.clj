(ns infra.http.ring-adapter-test
  (:require [utils.http-utils :as http-utils]
            [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test :refer [match?]]
            [application.http.http-server :as http-server]
            [infra.http.ring-adapter :as ring-adapter]))

(defn- handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello world"})

(deftest ^:integration listen
  (testing "should startup a http server"
    (let [config   {:port 3000}
          adapter  (ring-adapter/->ring-adapter handler)
          endpoint (str "http://localhost:" (:port config))
          server   (http-server/listen adapter config)]
      (is (match? {:status 200
                   :body   "Hello world"}
                  (http-utils/get-request endpoint)))
      (.stop server))))
