(ns tabula.system
  (:require [environ.core :refer [env]]
            [com.stuartsierra.component :as component]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [meta-merge.core :refer [meta-merge]]
            [ring.component.jetty :refer [jetty-server]]
            [ring.middleware.defaults :refer [wrap-defaults secure-api-defaults]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [tabula.middleware.facebook :as fb]
            [tabula.endpoint.home :refer [home-endpoint]]
            [tabula.endpoint.server :refer [server-endpoint]]
            [tabula.component.db :refer [database-component]]))

(def site-defaults
  {:static    {:resources "public"}
   :security  {:xss-protection {:enable? true, :mode :block}
               :content-type-options :nosniff}
   :proxy true})

(defn- wrap-site
  [handler db]
  (-> handler
      (fb/wrap-user db)
      wrap-anti-forgery ;; TODO: :error-handler
      fb/wrap-session
      fb/wrap-fix-request-method))

(def base-config
  {:http {:port 3000}
   :app  {:middleware [[wrap-not-found :not-found]
                       [wrap-site :db]
                       [wrap-defaults :defaults]]
          :not-found  "errors/404.html"
          :defaults   (meta-merge secure-api-defaults site-defaults)}
   :db   {:uri (-> env :db :uri)}})

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
         :http (jetty-server (:http config))
         :app  (handler-component (:app config))
         :home (endpoint-component home-endpoint)
         :server (endpoint-component server-endpoint)
         :db   (database-component (-> config :db :uri)))
        (component/system-using
         {:http [:app]
          :app  [:home :server :db]
          :home [:db]
          :server [:db]}))))
