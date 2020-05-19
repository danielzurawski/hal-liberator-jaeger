(ns items.items
  (:require [opencensus-clojure.trace :refer [span]]))

(defn get-all-items []
  (span "get-all-items" 
        [{:id 123 :name "Oranges"}
         {:id 456 :name "Apples"}]))