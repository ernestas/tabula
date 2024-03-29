(ns tabula.view.login
  (:require [hiccup.core :refer :all]
            [clj-oauth2.client :as oauth2-client]
            [tabula.config.facebook :as fb]))

(defn auth-dialog [request]
  (html
   [:script
    (str "window.top.location.href='"
         (:uri (oauth2-client/make-auth-request (fb/app-info request)))
         "'")]))
