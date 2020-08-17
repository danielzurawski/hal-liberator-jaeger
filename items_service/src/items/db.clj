(ns items.db
  (:require [opencensus-clojure.trace :refer [span]]))

(defn get-all-items []
  (span "get-all-items"
    {123 {:id 123 :name "Oranges"}
     456 {:id 456 :name "Apples"}}))
