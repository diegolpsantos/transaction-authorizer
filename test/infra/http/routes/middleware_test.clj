(ns infra.http.routes.middleware-test
  (:require [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test :refer [match?]]
            [infra.http.routes.middleware :as middleware]))

(def handler (fn [request]
               request))

(deftest wrap-dependencies
  (testing "should return a middleware to add dependencies"
    (let [middleware (middleware/wrap-dependencies {})
          request    (-> (:wrap middleware)
                         (apply [handler])
                         (apply [{:body {:name "name"}}]))]
      (is (match? {:name keyword?
                   :wrap fn?}
                  middleware))
      (is (match? {:deps map?}
                  request)))))