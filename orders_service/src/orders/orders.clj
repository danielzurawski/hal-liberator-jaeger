(ns orders.orders
  (:require [halboy.resource :as hal]
            [hype.core :as hype]
            [shared :as shared]
            [orders.order :as order]
            [orders.db :as db]))


(defn orders-to-resources [request routes orders]
  (map
    #(order/make-order-resource request routes %)
    orders))

(defn orders-liberator-hal-resource [routes]
  (shared/hal-resource-handler-for
    {}
    :allowed-methods [:get]

    :handle-ok
    (fn [{:keys [request]}]
      (let [orders (vals (db/get-all-orders))]
        (->
          (hal/new-resource (hype/absolute-url-for request routes :orders))
          (hal/add-resource :orders (orders-to-resources request routes orders)))))))
