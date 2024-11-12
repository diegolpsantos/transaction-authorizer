(ns infra.http.reitit-adapter
  (:require [application.http.router :as router]
            [reitit.ring :as ring]
            [reitit.openapi :as openapi]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.spec :as spec]
            [muuntaja.core :as m]
            [malli.util :as mu]
            [reitit.coercion.malli]))

(defn- create-routes [routes]
  (ring/ring-handler
   (ring/router
    [["/swagger.json"
      {:get {:no-doc true
             :swagger {:info {:title "payment-service"
                              :description "payment-service api"
                              :version "0.0.1"}
                       :tags [{:name "payment"
                               :description "payment api"}]}
             :handler (swagger/create-swagger-handler)}}]
     ["/openapi.json"
      {:get {:no-doc true
             :openapi {:info {:title "payment-service"
                              :description "payment-service api"
                              :version "0.0.1"}}
             :handler (openapi/create-openapi-handler)}}]

     ["/api/v1" routes]]

    {:validate spec/validate
     :exception pretty/exception
     :data {:coercion (reitit.coercion.malli/create
                       {:error-keys #{:coercion :in :schema :value :errors :humanized}
                        :compile mu/closed-schema
                        :strip-extra-keys true
                        :default-values true
                        :options nil})
            :muuntaja m/instance
            :middleware [swagger/swagger-feature
                         openapi/openapi-feature
                         parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         exception/exception-middleware
                         muuntaja/format-request-middleware
                         coercion/coerce-response-middleware
                         coercion/coerce-request-middleware
                         multipart/multipart-middleware]}})
   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path "/"
      :config {:validatorUrl nil
               :urls [{:name "swagger", :url "swagger.json"}
                      {:name "openapi", :url "openapi.json"}]
               :urls.primaryName "openapi"
               :operationsSorter "alpha"}})
    (ring/create-default-handler))))

(defrecord reitit-adapter [routes]
  router/router
  
  (create [_]
    (create-routes routes)))