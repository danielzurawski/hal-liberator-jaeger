(ns core
  (:require [bidi.ring :refer [make-handler]]
            [ring.adapter.jetty :as jetty]

            [telemetry.tracing :as tel-tracing]
            [telemetry.middleware :refer [wrap-telemetry-tracing]]

            [discovery :as discovery]
            [ping :as ping]
            [orders.order :as order]
            [orders.orders :as orders]))


(def routes ["/" {""        :discovery
                  "ping"    :ping
                  "orders/" {""          :orders
                             [:order-id] :order}}])


(defn make-resource-handlers []
  {:discovery (discovery/discovery-liberator-hal-resource routes)
   :ping      (ping/ping-liberator-hal-resource routes)

   :orders    (orders/orders-liberator-hal-resource routes)
   :order     (order/order-liberator-hal-resource routes)})


(defn go []
  (println "Starting server")

  (tel-tracing/setup-span-processor
    (tel-tracing/create-spans-processor-jaeger "orders" "localhost" "14268"))

  (jetty/run-jetty
    (->
      (make-handler routes (make-resource-handlers))
      (wrap-telemetry-tracing))
    {:port 80 :join? false})

  (println "Server started"))
