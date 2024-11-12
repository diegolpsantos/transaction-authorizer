(ns infra.http.ring-adapter
  (:require [application.http.http-server :as server] 
            [ring.adapter.jetty :as jetty]
            [reitit.coercion.malli]
            [reitit.ring.malli]))

(defrecord ring-adapter [routes]
  server/http-server
  
  (listen [_ {:keys [port join?]}]
    (jetty/run-jetty routes {:port  port
                             :join? join?})))