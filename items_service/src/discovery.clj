(ns discovery
  (:require [shared :as shared]
            [hype.core :as hype]
            [halboy.resource :as hal]))

(defn discovery-liberator-hal-resource [routes]
  (shared/hal-resource-handler-for
    {}
    :allowed-methods [:get]
    :handle-ok
    (fn [{:keys [request]}]
      (->
        (hal/new-resource {:href (hype/absolute-url-for request routes :discovery)})
        (hal/add-links
          :items {:href (hype/absolute-url-for request routes :items)}
          :item {:href (hype/absolute-url-for request routes :item
                         {:path-params {:item-id "{itemId}"}})})))))
