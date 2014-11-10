(ns tabula.config.facebook
  (:require [environ.core :refer [env]]
            [ring.util.response :refer [get-header]]
            [ring.util.request :refer [request-url]]))

(defn app-info
  [& [request]]
  {:authorization-uri "https://www.facebook.com/v2.2/dialog/oauth"
   :access-token-uri "https://graph.facebook.com/v2.2/oauth/access_token"
   :redirect-uri (cond (nil? request) ""
                       (-> request :params :signed_request)
                       (get-header request "referer")
                       :else (request-url request))
   :client-id (-> env :facebook :client-id)
   :client-secret (-> env :facebook :client-secret)
   :access-query-param :access_token
   :scope ["public_profile" "user_friends"]
   :grant-type "authorization_code"})
