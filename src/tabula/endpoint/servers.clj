(ns tabula.endpoint.servers
  (:require [compojure.core :refer :all]))

(defn servers-endpoint [{:keys [db]}]
  (routes
   (GET "/" request (str request "<br><br>" (:conn db)))))
