(ns items.item
  (:require [hype.core :as hype]
            [halboy.resource :as hal]
            [shared :as shared]
            [items.db :as db]))

(defn get-item-link [request routes id]
  (hype/absolute-url-for request routes :item
    {:path-params {:item-id id}}))

(defn make-item-resource [request routes item]
  (->
    (hal/new-resource (get-item-link request routes (:id item)))
    (hal/add-properties
      :name (:name item)
      :id (:id item))))

(defn item-liberator-hal-resource [routes]
  (shared/hal-resource-handler-for
    {}
    :allowed-methods [:get]

    :initialize-context
    (fn [context]
      {:item-id (get-in context [:request :params :item-id])})

    :exists?
    (fn [{:keys [item-id]}]
      (when-let [item (get (db/get-all-items) item-id)]
        {:item item}))

    :handle-ok
    (fn [{:keys [request item-id]}]
      (let [item (get (db/get-all-items) item-id)]
        (make-item-resource request routes item)))))
