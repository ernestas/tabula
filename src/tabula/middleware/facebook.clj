(ns tabula.middleware.facebook
  (:require [ring.util.response :as response]
            [ring.middleware.session :refer [session-request session-response]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [clj-facebook-graph.auth :refer [decode-signed-request]]
            [clj-oauth2.client :as oauth2-client]
            [tabula.config.facebook :refer [app-info]]
            [tabula.model.facebook :as facebook]
            [tabula.model.user :as user]
            [tabula.view.login :refer [auth-dialog]]))

(defn wrap-user
  [handler db]
  (fn [{{user-id :id {access-token :access-token} :oauth2 :as session}
        :session
        :as request}]
    (if user-id
      (handler request)
      (let [user-data (facebook/me access-token)
            new-session (merge session user-data)]
        (user/add db user-data)
        (-> request
            (assoc :session new-session)
            handler
            (assoc :session new-session))))))

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

(defn- timestamp-to-max-age
  [timestamp]
  (- (Integer. timestamp)
     (int (/ (System/currentTimeMillis) 1000))))

(defn- new-access-token-info
  "Returns {:access-token \"123\" :max-age 456}

NOTE: :expires key in signed-request is a timestamp, but :expires key in a map
returned by oauth2-client is a max-age (time in seconds till the expiration)"
  [request]
  (let [sr (get-signed-request request)
        {access-token :oauth_token, timestamp :expires} sr]
    (if access-token
      {:access-token access-token
       :max-age (timestamp-to-max-age timestamp)}
      (when-let [code (get-code-map request sr)]
        (let [{access-token :access-token, {max-age :expires} :params}
              (oauth2-client/get-access-token (app-info request) code)]
          {:access-token access-token
           :max-age (Integer. max-age)})))))

;; TODO: remove
(defn- session-options
  [options]
  {:store        (options :store (cookie-store {:key "1234567812345678"}))
   :cookie-name  (options :cookie-name "ring-session")
   :cookie-attrs (merge {:path "/"
                         :http-only true}
                        (options :cookie-attrs)
                        (if-let [root (options :root)]
                          {:path root}))})

(defn wrap-session
  [handler]
  (fn [request]
    (let [options {:cookie-attrs {:secure true}} ;; TODO: carmine-store
          options (session-options options) ;; TODO: remove
          new-request (session-request request options)
          old-token (-> new-request :session :oauth2)]
      (if old-token
        (handler new-request)
        (if-let [new-token (new-access-token-info new-request)]
          (let [options (assoc-in options
                                  [:cookie-attrs :max-age]
                                  (new-token :max-age))]
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
