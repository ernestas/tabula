(ns tabula.endpoint.home
  (:require [compojure.core :refer :all]
            [tabula.model.user :as user]))

(defn home-endpoint [{:keys [db]}]
  (routes
   (GET "/" request (str request "<br><br>"
                         (user/get-user db (-> request :session :user-id))))))
