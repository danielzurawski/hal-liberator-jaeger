(ns items.items
  (:require [halboy.resource :as hal]
            [items.db :as db]
            [items.item :as item]
            [shared :as shared]))

(defn items-to-resources [request routes items]
  (map
    #(item/make-item-resource request routes %)
    items))

(defn items-liberator-hal-resource [routes]
  (shared/hal-resource-handler-for
    {}
    :allowed-methods [:get]

    :handle-ok
    (fn [{:keys [request]}]
      (->
        (hal/new-resource "/items")
        (hal/add-resource :items
          (items-to-resources request routes (vals (db/get-all-items))))))))
