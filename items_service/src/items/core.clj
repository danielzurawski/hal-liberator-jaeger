(ns items.core
  (:require [halboy.resource :as hal]
            [bidi.ring :refer [make-handler]]
            [hype.core :as hype]
            [ring.util.response :as res]

            [opencensus-clojure.trace :refer [span add-tag make-downstream-headers]]
            [opencensus-clojure.ring.middleware :refer [wrap-tracing]]
            [opencensus-clojure.reporting.jaeger]
            [opencensus-clojure.reporting.logging]

            [liberator-mixin.core :refer [build-resource]]
            [liberator-mixin.json.core :refer [with-json-mixin]]
            [liberator-mixin.validation.core :refer [with-validation-mixin]]
            [liberator-mixin.hypermedia.core :refer [with-hypermedia-mixin]]
            [liberator-mixin.hal.core :refer [with-hal-mixin]]

            [items.items :refer [get-all-items]])
  (:use ring.adapter.jetty))

(def routes ["" [["/" :discovery]
                 ["/items" :items]]])

(defn hal-resource-handler-for [dependencies & {:as overrides}]
  (build-resource
    (with-json-mixin dependencies)
    (with-validation-mixin dependencies)
    (with-hypermedia-mixin dependencies)
    (with-hal-mixin dependencies)
    overrides))

(def discovery-liberator-hal-resource
  (hal-resource-handler-for
    {}
    :allowed-methods [:get]
    :handle-ok
    (fn [{:keys [request]}]
      (hal/add-links
        (hal/new-resource {:href (hype/absolute-url-for request routes :discovery)})
        :items {:href (hype/absolute-url-for request routes :items)}))))



(def items-liberator-hal-resource
  (hal-resource-handler-for
    {}
    :allowed-methods [:get]
    :handle-ok
    (fn [{:keys [request]}]
      (-> (hal/new-resource "/items")
        (hal/add-resource :items (map #(-> (hal/new-resource (str "/items/" (:id %)))
                                         (hal/add-property :name (:name %)))
                                   (get-all-items)))))))


(defn make-resource-handlers []
  {:discovery discovery-liberator-hal-resource
   :items     items-liberator-hal-resource})



(def handler
  (-> (make-handler routes (make-resource-handlers))
    (wrap-tracing :uri)))


(defn go []
  (println "Starting server")

  (opencensus-clojure.trace/configure-tracer {:probability 1.0})
  (opencensus-clojure.reporting.logging/report)
  (opencensus-clojure.reporting.jaeger/report "http://hal-jaeger:14268/api/traces" "items-service")

  (run-jetty handler {:port 80 :join? false})

  (println "Server started"))
