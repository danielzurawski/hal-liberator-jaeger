(ns orders.items.items_service
  (:require [opencensus-clojure.trace :refer [span add-tag make-downstream-headers]]
            [halboy.navigator :as navigator]
            [halboy.resource :as hal]
            [http :as http]))

(defn get-items []
  (span "get-items"
    (let [headers (make-downstream-headers)]
      (->
        (navigator/discover "http://items-service:3032/"
          {:headers headers
           :client  (http/new-http-client)})
        (assoc-in [:settings :http :headers] headers)
        (navigator/get :items)))))

