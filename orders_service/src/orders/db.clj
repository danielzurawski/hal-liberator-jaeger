(ns orders.db
  (:require [opencensus-clojure.trace :refer [span]]))

(defn get-all-orders []
  (span "get-all-orders"
    {123 {:id 123 :name "Apples & Oranges Order"}}))
