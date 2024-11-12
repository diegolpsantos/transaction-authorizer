(ns utils.http-utils
  (:require [clj-http.client :as chc]
            [cheshire.core :as json]))

(defn- option [body]
  {:body             (json/encode body)
   :content-type     :json
   :accept           :json
   :throw-exceptions false})

(defn post [endpoint body]
  (->> body
       option
       (chc/post endpoint)))

(defn patch [endpoint body]
  (->> body
       option
       (chc/patch endpoint)))

(defn get-request [endpoint]
  (chc/get endpoint))

(defn delete [endpoint body]
  (->> body
       option
       (chc/delete endpoint)))

(defn str-body->map [response]
  (-> response
      :body
      (json/decode true)))