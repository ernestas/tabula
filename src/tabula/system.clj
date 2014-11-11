(ns tabula.system
  (:require [environ.core :refer [env]]
            [com.stuartsierra.component :as component]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [meta-merge.core :refer [meta-merge]]
            [ring.component.jetty :refer [jetty-server]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [tabula.middleware.facebook :refer [wrap-fix-request-method
                                                wrap-session]]
            [tabula.endpoint.home :refer [home-endpoint]]
            [tabula.component.db :refer [database-component]]))

(def site-defaults
  {:static    {:resources "public"}
   :security  {:xss-protection {:enable? true, :mode :block}
               :content-type-options :nosniff}
   :proxy true})

(defn- wrap-site
  [handler]
  (-> handler
      wrap-anti-forgery ;; TODO: :error-handler
      wrap-session
      wrap-fix-request-method))

(def base-config
  {:http {:port 3000}
   :app  {:middleware [[wrap-not-found :not-found]
                       wrap-site
                       [wrap-defaults :defaults]]
          :not-found  "errors/404.html"
          :defaults   (meta-merge api-defaults site-defaults)}
   :db   {:uri (-> env :db :uri)}})

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
         :app  (handler-component (:app config))
         :http (jetty-server (:http config))
         :home (endpoint-component home-endpoint)
         :db   (database-component (-> config :db :uri)))
        (component/system-using
         {:http [:app]
          :app  [:home]
          :home [:db]}))))
