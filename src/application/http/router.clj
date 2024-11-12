(ns application.http.router)

(defprotocol router
  (create [this]))