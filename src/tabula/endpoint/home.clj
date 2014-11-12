(ns tabula.endpoint.home
  (:require [compojure.core :refer :all]
            [tabula.model.user :as user]
            [tabula.view.home :as home]))

(defn home-endpoint [{:keys [db]}]
  (routes
   (GET "/" request (str (home/draw request db)
                         "<br><br>" request "<br><br>"
                         (user/get-user db (-> request :session :id))))))
