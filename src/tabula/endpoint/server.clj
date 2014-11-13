(ns tabula.endpoint.server
  (:require [compojure.core :refer :all]
            [tabula.view.server :as server-view]))

(defn server-endpoint [{:keys [db]}]
  (context
   ;; TODO: only match valid server names
   "/server/:server-id" [server-id]
   (GET "/" request (server-view/draw server-id request db))))
