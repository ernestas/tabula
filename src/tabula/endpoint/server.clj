(ns tabula.endpoint.server
  (:require [ring.util.response :refer [redirect]]
            [compojure.core :refer [context GET wrap-routes]]
            [tabula.model.player :as player]
            [tabula.view.server :as server-view]
            [tabula.view.server.registration :as registration]))

(defn handler [db]
  (context
   ;; TODO: only match valid server names
   "/server/:server" [server]
   (GET "/" request (server-view/draw server request db))
   (registration/routes db server-id)))

(defn wrap-register
  [db]
  (fn [handler]
    (fn [{{server-id :server-id} :params, {user-id :id} :session
          request-method :request-method, uri :uri, :as request}]
      (let [registration-route (str "/server/" server-id "/registration")
            player-tribe (player/tribe db user-id server-id)]
        (if (= uri registration-route)
          (if player-tribe
            (redirect (str "/server/" server-id))
            (handler request))
          (if player-tribe
            (handler request)
            (redirect registration-route)))))))

(defn server-endpoint [{:keys [db]}]
  (wrap-routes (handler db)
               (wrap-register db)))
