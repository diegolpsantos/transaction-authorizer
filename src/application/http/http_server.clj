(ns application.http.http-server)

(defprotocol http-server
  (listen [this config]))