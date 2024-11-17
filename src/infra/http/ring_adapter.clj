(ns infra.http.ring-adapter
  (:require [application.http.http-server :as server]
            [ring.adapter.jetty :as jetty]
            [reitit.coercion.malli]
            [reitit.ring.malli]
            [ring.logger :as logger]))

(defrecord ring-adapter [routes]
  server/http-server

  (listen [_ {:keys [port join?]}]
    (jetty/run-jetty (logger/wrap-with-logger routes)
                     {:port  port
                      :join? join?})))