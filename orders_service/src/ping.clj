(ns ping
  (:require [halboy.resource :as hal]
            [hype.core :as hype]
            [shared :as shared]))

(defn ping-liberator-hal-resource [routes]
  (shared/hal-resource-handler-for
    {}
    :allowed-methods [:get]
    :handle-ok
    (fn [{:keys [request]}]
      (->
        (hal/new-resource {:href (hype/absolute-url-for request routes :ping)})
        (hal/add-property :message "pong")))))
