(ns tabula.endpoint.home
  (:require [compojure.core :refer :all]))

(defn home-endpoint [{:keys [db]}]
  (routes
   (GET "/" request (str request "<br><br>" (:conn db)))))
