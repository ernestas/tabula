(ns tabula.middleware.facebook
  (:require [ring.util.response :as response]
            [ring.middleware.session :refer [session-request session-response]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [clj-facebook-graph.auth :refer [decode-signed-request]]
            [clj-oauth2.client :as oauth2-client]
            [tabula.config.facebook :refer [app-info]]
            [tabula.view.login :refer [auth-dialog]]))

(defn- get-code-map
  [request sr]
  (when-let [code (or (-> request :params :code)
                      (-> sr :code))]
    {:code code}))

(defn- get-signed-request
  [request]
  (decode-signed-request
   (or (get-in request [:params :signed_request])
       (get-in request [:cookies (str "fbsr_" ((app-info) :client-id)) :value]))
   ((app-info) :client-secret)))

(defn- new-access-token-info
  "Returns {:token '123' :expires '456'}"
  [request]
  (let [sr (get-signed-request request)
        {:keys [:oauth_token :expires]} sr]
    (if oauth_token
      {:access-token oauth_token
       :expires expires}
      (when-let [code (get-code-map request sr)]
        (let [{access-token :access-token, {expires :expires} :params}
              (oauth2-client/get-access-token (app-info request) code)]
          {:access-token access-token
           :expires expires})))))

;; TODO: remove
(defn- session-options
  [options]
  {:store        (options :store (cookie-store))
   :cookie-name  (options :cookie-name "ring-session")
   :cookie-attrs (merge {:path "/"
                         :http-only true}
                        (options :cookie-attrs)
                        (if-let [root (options :root)]
                          {:path root}))})

(defn- timestamp-to-max-age
  [timestamp]
  (- timestamp
     (int (/ (System/currentTimeMillis) 1000))))

(defn wrap-session
  [handler]
  (fn [request]
    (let [options {} ;; TODO: carmine-store
          options (session-options options) ;; TODO: remove
          new-request (session-request request options)
          old-token (-> new-request :session :oauth2)]
      (if old-token
        (handler new-request)
        (if-let [new-token (new-access-token-info new-request)]
          (let [options (assoc-in options
                                  [:cookie-attrs :max-age]
                                  (timestamp-to-max-age (new-token :expires)))]
            (-> new-request
                (assoc-in [:session :oauth2] new-token)
                handler
                (assoc-in [:session :oauth2] new-token)
                (session-response new-request options)))
          (-> (response/response (auth-dialog request))
              (response/content-type "text/html")))))))

(defn wrap-fix-request-method
  [handler]
  (fn [request]
    (if (and (= :post (:request-method request))
             (-> request :params :signed_request))
      (handler (assoc request :request-method :get))
      (handler request))))
