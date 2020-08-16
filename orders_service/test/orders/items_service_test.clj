(ns orders.items-service-test
  (:require
    [clojure.test :refer :all]
    [clj-http.fake :as fake]
    [orders.items_service :as items-service]
    [halboy.navigator :as navigator]
    [jsonista :as json]))

(def stub-discovery
  (fn [request]

    (println request)
    {:status  200
     :headers {}
     :body    "{}"}))

(def items-stub-fn
  (fn [request]
    (println request)
    {:status  200
     :headers {}
     :body    "{}"}))

(deftest test-halboy-clj-http
  (fake/with-global-fake-routes-in-isolation
    {#".*"
     (fn [request]
       (println request)
       {:status  200
        :headers {}
        :body    "{}"})}

    (let [items-result (items-service/get-items {})]
      (testing "returns 200"
        (is (= 200 (navigator/status items-result)))))))
