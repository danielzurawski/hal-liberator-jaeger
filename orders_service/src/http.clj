(ns http
  (:require
    [halboy.http.protocol :as protocol]
    [clj-http.client :as client]))

(defn format-for-halboy [response url]
  (merge
    (select-keys response [:error :body :headers :status])
    {:url url
     :raw response}))

(defn new-http-client []
  (reify protocol/HttpClient
    (exchange [_ {:keys [url method] :as request}]
      (->
        (client/request (merge {:as :json} request))
        (format-for-halboy url)))))
