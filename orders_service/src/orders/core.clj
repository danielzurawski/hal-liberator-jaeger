(ns orders.core
  (:require [halboy.resource :as hal]
            [bidi.ring :refer [make-handler]]
            [hype.core :as hype]
            [clj-http.client :as client]

            [opencensus-clojure.ring.middleware :refer [wrap-tracing]]
            [opencensus-clojure.reporting.jaeger]
            [opencensus-clojure.reporting.logging]

            [liberator-mixin.core :refer [build-resource]]
            [liberator-mixin.json.core :refer [with-json-mixin]]
            [liberator-mixin.validation.core :refer [with-validation-mixin]]
            [liberator-mixin.hypermedia.core :refer [with-hypermedia-mixin]]
            [liberator-mixin.hal.core :refer [with-hal-mixin]]
            [orders.items_service :as items])
  (:use ring.adapter.jetty))

(def routes ["" [["/" :discovery]
                 ["/orders/123" :orders]]])

(defn hal-resource-handler-for [dependencies & {:as overrides}]
  (build-resource
    (with-json-mixin dependencies)
    (with-validation-mixin dependencies)
    (with-hypermedia-mixin dependencies)
    (with-hal-mixin dependencies)
    overrides))

(def orders-liberator-hal-resource
  (hal-resource-handler-for
    {}
    :allowed-methods [:get]
    :handle-ok (fn [{:keys [request]}]
                 (let [items-resource (items/get-items 123)
                       _ (println "retrieved items" items-resource)]
                   (-> (hal/new-resource "/orders/123")
                     (hal/add-resource :items items-resource))))))

(def discovery-liberator-hal-resource
  (hal-resource-handler-for
    {}
    :allowed-methods [:get]
    :handle-ok (fn [{:keys [request]}]
                 (hal/add-links
                   (hal/new-resource {:href (hype/absolute-url-for request routes :discovery)})
                   :orders {:href (hype/absolute-url-for request routes :orders)}))))


(defn make-resource-handlers []
  {:orders    orders-liberator-hal-resource
   :discovery discovery-liberator-hal-resource})


(def handler
  (-> (make-handler routes (make-resource-handlers))
    (wrap-tracing (fn [req] (-> req :uri)))))


(defn go []
  (println "Starting server")

  (opencensus-clojure.trace/configure-tracer {:probability 1.0})
  (opencensus-clojure.reporting.logging/report)
  (opencensus-clojure.reporting.jaeger/report "http://hal-jaeger:14268/api/traces" "orders-service")

  (run-jetty handler {:port 80 :join? false})

  (println "Server started"))
