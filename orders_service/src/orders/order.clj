(ns orders.order
  (:require [halboy.resource :as hal]
            [hype.core :as hype]
            [shared :as shared]
            [orders.items.items_service :as items]
            [orders.db :as db]))


(defn get-order-link [request routes order-id]
  (hype/absolute-url-for request routes :order
    {:path-params {:order-id order-id}}))

(defn make-order-resource [request routes order]
  (->
    (hal/new-resource (get-order-link request routes (:id order)))
    (hal/add-properties
      :id (:io order)
      :name (:name order))))

(defn order-liberator-hal-resource [routes]
  (shared/hal-resource-handler-for
    {}
    :allowed-methods [:get]

    :initialize-context
    (fn [context]
      {:order-id (get-in context [:request :params :order-id])})

    :exists?
    (fn [{:keys [order-id]}]
      (when-let [order (get (db/get-all-orders) order-id)]
        {:order order}))

    :handle-ok
    (fn [{:keys [request order-id order]}]
      (let [items-resource (items/get-items order-id)]
        (->
          (make-order-resource request routes order)
          (hal/add-resources items-resource))))

    :handle-not-found
    (fn [{:keys [request order-id]}]
      (hal/add-properties
        (hal/new-resource (get-order-link request routes order-id))

        :message (format "Order %s does not exist" order-id)))

    :handle-exception
    (fn [{:keys [request order-id]}]
      (hal/add-properties
        (hal/new-resource (get-order-link request routes order-id))

        :message (format "Unknown exception. Order %s could not be fetched" order-id)))))
