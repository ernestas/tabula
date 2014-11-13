(ns tabula.endpoint.home
  (:require [compojure.core :refer :all]
            [tabula.view.home :as home]))

(defn home-endpoint [{:keys [db]}]
  (routes
   (GET "/" request (home/draw request db))))
