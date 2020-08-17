(ns discovery
  (:require [halboy.resource :as hal]
            [hype.core :as hype]
            [shared :as shared]))

(defn discovery-liberator-hal-resource [routes]
  (shared/hal-resource-handler-for
    {}
    :allowed-methods [:get]
    :handle-ok
    (fn [{:keys [request]}]
      (->
        (hal/new-resource {:href (hype/absolute-url-for request routes :discovery)})

        (hal/add-links
          :ping {:href (hype/absolute-url-for request routes :ping)}

          :orders {:href (hype/absolute-url-for request routes :orders)}
          :order {:templated true
                  :href      (hype/absolute-url-for request routes
                               :order {:path-params {:order-id "{orderId}"}})})))))

