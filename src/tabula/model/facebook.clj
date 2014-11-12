(ns tabula.model.facebook
  (:require [clj-oauth2.client :as oauth2]
            [tabula.config.facebook :as fb]))

(defn- query
  [path query-params access-token]
  (oauth2/read-json-from-body
   (:body (oauth2/get (str ((fb/app-info) :graph-uri) path)
                      {:query-params (merge {:access_token access-token}
                                            query-params)}))))

(defn me
  [access-token]
  (query "/me" {:fields "id,first_name,locale,friends"} access-token))
