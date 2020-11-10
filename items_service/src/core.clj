(ns core
  (:require [bidi.ring :refer [make-handler]]
            [ring.adapter.jetty :as jetty]

            [telemetry.tracing :as tel-tracing]
            [telemetry.middleware :refer [wrap-telemetry-tracing]]

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

  (tel-tracing/setup-span-processor
    (tel-tracing/create-spans-processor-jaeger "items" "localhost" "14268"))

  (jetty/run-jetty
    (->
      (make-handler routes (make-resource-handlers))
      (wrap-telemetry-tracing))
    {:port 80 :join? false})

  (println "Server started"))
