(ns tabula.view.server.registration
  (:require [ring.util.response :refer [redirect]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [compojure.core :refer [GET POST] :as compojure]
            [hiccup.form :refer (form-to radio-button submit-button)]
            [hiccup.core :refer [html]]
            [tabula.model.player :as player]
            [tabula.view.page :as page]))

(defn view
  [uri]
  (page/layout
         (html [:h2 "Registration"]
               (form-to [:post uri]
                        (anti-forgery-field)
                        (radio-button "tribe" false "1") "Attacking"
                        (radio-button "tribe" false "2") "Defensive"
                        (radio-button "tribe" false "3") "Economic"
                        (submit-button "Play!")))))

(defn routes
  [db server-name]
  (compojure/routes
   (GET "/registration"
        {uri :uri}
        (view uri))
   (POST "/registration"
         [tribe :as {{user-id :id} :session, uri :uri, :as request}]
         ;; TODO: validation
         (if (seq tribe)
           (do @(player/add db user-id server-name tribe)
               (redirect (str "/server/" server-name)))
           (view uri)))))
