(ns tabula.view.server.registration
  (:require [ring.util.response :refer [redirect]]
            [compojure.core :refer [GET POST] :as compojure]
            [hiccup.core :refer [html]]
            [tabula.model.player :as player]
            [tabula.view.page :as page]))

(defn view
  [uri]
  (page/layout
         (html [:h2 "Registration"]
               [:a {:href (str uri "/tribe/1")} "Attacking"]
               [:a {:href (str uri "/tribe/2")} "Defensive"]
               [:a {:href (str uri "/tribe/3")} "Economic"])))

(defn routes
  [db server-name]
  (compojure/routes
   (GET "/registration"
        {uri :uri}
        (view uri))
   (GET ["/registration/tribe/:tribe-id", :tribe-id #"[1-3]"]
        [tribe-id :as {{user-id :id} :session, uri :uri, :as request}]
        (if (seq tribe-id)
          (do @(player/add db user-id server-name tribe-id)
              (redirect (str "/server/" server-name)))
          (view uri)))))
