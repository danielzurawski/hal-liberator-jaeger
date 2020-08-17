(ns core
  (:require [bidi.ring :refer [make-handler]]
            [ring.adapter.jetty :as jetty]

            [opencensus-clojure.ring.middleware :refer [wrap-tracing]]
            [opencensus-clojure.trace :refer [configure-tracer]]
            [opencensus-clojure.reporting.jaeger :as open-consensus-jaeger]
            [opencensus-clojure.reporting.logging :as open-consensus-logging]

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

  (configure-tracer {:probability 1.0})
  (open-consensus-logging/report)
  (open-consensus-jaeger/report "http://hal-jaeger:14268/api/traces" "orders-service")

  (jetty/run-jetty
    (->
      (make-handler routes (make-resource-handlers))
      (wrap-tracing (fn [req] (-> req :uri))))
    {:port 80 :join? false})

  (println "Server started"))
