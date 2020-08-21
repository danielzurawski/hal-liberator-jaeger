(ns orders.items.items-service-test
  (:require [clojure.test :refer :all]
            [halboy.resource :as hal]
            [halboy.json :as haljson]
            [jason.convenience :as json]
            [clj-http.fake :as fake]

            [orders.items.items_service :as items]
            [halboy.navigator :as navigator]))


(def items [{:id 123 :name "Oranges"}
            {:id 456 :name "Apples"}])

(defn- item-to-resource [item]
  (->
    (hal/new-resource (str "http://items-service/item/" (:id item)))
    (hal/add-properties
      :name (:name item)
      :id (:id item))))

(defn items-service-discovery-stub [_]
  {:status 200
   :body   (->
             (hal/new-resource)
             (hal/add-href :items "http://items-service/items")
             (hal/add-link :item {:templated true :href "http://items-service/item/{itemId"})
             (haljson/resource->map)
             (json/->wire-json))})

(defn items-service-items-stub [_]
  {:status 200
   :body   (->
             (hal/new-resource "http://items-service/items")
             (hal/add-resources :items (map item-to-resource items))
             (haljson/resource->map)
             (json/->wire-json))})


(deftest items-service-GET-on-success
  (fake/with-fake-routes
    {#".*/"      items-service-discovery-stub
     #".*/items" items-service-items-stub}
    (let [items-result (items/get-items)
          items-resources (hal/get-resource (navigator/resource items-result) :items)]
      (testing "returns status code 200"
        (is (= 200 (navigator/status items-result))))
      (testing "returns items with correct props"
        (is (= items (map hal/properties items-resources)))))))
