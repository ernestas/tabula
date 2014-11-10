(ns tabula.endpoint.servers-test
  (:require [clojure.test :refer :all]
            [tabula.endpoint.servers :as servers]))

(def handler
  (servers/servers-endpoint {}))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
