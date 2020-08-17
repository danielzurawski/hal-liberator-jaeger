(ns core
  (:require [bidi.ring :refer [make-handler]]
            [ring.adapter.jetty :as jetty]

            [opencensus-clojure.ring.middleware :refer [wrap-tracing]]
            [opencensus-clojure.trace :refer [configure-tracer]]
            [opencensus-clojure.reporting.jaeger :as open-consensus-jaeger]
            [opencensus-clojure.reporting.logging :as open-consensus-logging]

            [discovery :as discovery]
            [items.item :as item]
            [items.items :as items]))

(def routes ["/" {""       :discovery
                  "ping"   :ping
                  "items/" {""         :items
                            [:item-id] :item}}])

(defn make-resource-handlers []
  {:discovery (discovery/discovery-liberator-hal-resource routes)

   :items     (items/items-liberator-hal-resource routes)
   :item      (item/item-liberator-hal-resource routes)})


(defn go []
  (println "Starting server")

  (configure-tracer {:probability 1.0})
  (open-consensus-logging/report)
  (open-consensus-jaeger/report "http://hal-jaeger:14268/api/traces" "items-service")

  (jetty/run-jetty
    (->
      (make-handler routes (make-resource-handlers))
      (wrap-tracing :uri))
    {:port 80 :join? false})

  (println "Server started"))
