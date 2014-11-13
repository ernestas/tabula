(ns tabula.middleware.home
  (:require [ring.util.response :refer [redirect]]))

(defn wrap-go-to-server
  [handler]
  (fn [{uri :uri :as request}]
    (if (= uri "/")
      ;; FIXME:
      (redirect "/server/17592186045418")
      (handler request))))
