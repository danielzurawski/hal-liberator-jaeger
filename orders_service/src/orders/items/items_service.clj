(ns orders.items.items_service
  (:require [opencensus-clojure.trace :refer [span add-tag make-downstream-headers]]
            [telemetry.tracing :as tel-tracing]
            [halboy.navigator :as navigator]
            [halboy.resource :as hal]))

(defn get-items [order]
  (span "get-items"
    (let [headers (make-downstream-headers)
          span (tel-tracing/get-current-span)]
      (-> (navigator/discover "http://items-service/" {:headers headers})
        (assoc-in [:settings :http :headers] headers)
        (navigator/get :items)
        (navigator/resource)
        (hal/resources)))))

