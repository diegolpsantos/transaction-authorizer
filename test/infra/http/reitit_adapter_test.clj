(ns infra.http.reitit-adapter-test
  (:require [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test :refer [match?]]
            [application.http.router :as router-port]
            [infra.http.reitit-adapter :as router-adapter]))

(def route-example [["/test"
                     {:get
                      {:handler (fn [_]
                                  {:status 200
                                   :body   "hello world"})}}]])

(deftest create-route
  (testing "should be possible creating a route"
    (let [route (router-port/create (router-adapter/->reitit-adapter route-example))]
      (is (match? {:status 200
                   :body   "hello world"}
                  (route {:request-method :get
                          :uri            "/api/v1/test"}))))))