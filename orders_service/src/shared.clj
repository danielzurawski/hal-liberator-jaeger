(ns shared
  (:require [liberator-mixin.core :refer [build-resource]]
            [liberator-mixin.json.core :refer [with-json-mixin]]
            [liberator-mixin.validation.core :refer [with-validation-mixin]]
            [liberator-mixin.hypermedia.core :refer [with-hypermedia-mixin]]
            [liberator-mixin.hal.core :refer [with-hal-mixin]]))

(defn hal-resource-handler-for [dependencies & {:as overrides}]
  (build-resource
    (with-json-mixin dependencies)
    (with-validation-mixin dependencies)
    (with-hypermedia-mixin dependencies)
    (with-hal-mixin dependencies)
    overrides))


