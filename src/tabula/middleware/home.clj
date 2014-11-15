(ns tabula.middleware.home
  (:require [ring.util.response :refer [redirect]]))

(defn wrap-go-to-server
  [handler]
  (fn [{uri :uri :as request}]
    (if (= uri "/")
      (redirect "/server/lt1")
      (handler request))))
