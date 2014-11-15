(ns tabula.endpoint.server
  (:require [ring.util.response :refer [redirect]]
            [compojure.core :refer [context GET wrap-routes]]
            [tabula.model.player :as player]
            [tabula.view.server :as server-view]
            [tabula.view.server.registration :as registration]))

(defn handler [db]
  (context
   ;; TODO: only match valid server names
   "/server/:server-name" [server-name]
   (GET "/" request (server-view/draw server-name request db))
   (registration/routes db server-name)))

(defn wrap-register
  [db]
  (fn [handler]
    (fn [{{server-name :server-name} :params, {user-id :id} :session
          request-method :request-method, uri :uri, :as request}]
      (let [registration-route (str "/server/" server-name "/registration")
            player-tribe (player/tribe db user-id server-name)]
        (if (= uri registration-route)
          (if player-tribe
            (redirect (str "/server/" server-name))
            (handler request))
          (if player-tribe
            (handler request)
            (redirect registration-route)))))))

(defn server-endpoint [{:keys [db]}]
  (wrap-routes (handler db)
               (wrap-register db)))
