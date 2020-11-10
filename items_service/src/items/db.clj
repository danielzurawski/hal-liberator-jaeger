(ns items.db
  (:require [telemetry.tracing :as tracing]))

(defn get-all-items []
  (let [span (tracing/create-span)]
    (tracing/add-event span "get-all-items")
    {123 {:id 123 :name "Oranges"}
     456 {:id 456 :name "Apples"}}))
