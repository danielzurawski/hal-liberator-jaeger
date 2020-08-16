(ns orders.items_service
  (:require [opencensus-clojure.trace :refer [span add-tag make-downstream-headers]]
            [halboy.navigator :as navigator]
            [clj-http.client :as client]
            [halboy.http.protocol :as protocol]))

(defn- format-for-halboy [response]
  (merge
    (select-keys response [:error :body :headers :status])
    {:url (get-in response [:opts :url])
     :raw response}))

(defn make-halboy-client []
  (reify protocol/HttpClient
    (exchange [_ {:keys [url method] :as request}]
      (->
        (client/request (merge {:as :json} request))
        (format-for-halboy)))
    #_(let [request (-> request
                      (with-default-options)
                      (with-transformed-params)
                      (with-json-body))
            http-fn (http-method->fn method)]
        (-> @(http-fn url request)
          (parse-json-response)
          (format-for-halboy)))
    ))

(defn get-items [order]
  (span "get-items"
    (let [headers (make-downstream-headers)]
      (-> (navigator/discover "http://items-service/"
            {:client  (make-halboy-client)
             :headers headers})
        (assoc-in [:settings :http :headers] headers)
        (navigator/get :items)
        (navigator/resource)))))

