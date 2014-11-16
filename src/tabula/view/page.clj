(ns tabula.view.page
  (:require [environ.core :refer [env]]
            [hiccup.page :refer [html5 include-js include-css]]))

(defn layout [body]
  (html5 {:lang "en"}
         [:head
          [:meta {:charset "utf-8"}]
          [:meta {:name "viewport"
                  :content "width=device-width, initial-scale=1"}]
          [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
          [:title "Tabula"]
          ;; TODO: include project version string
          (include-css (str "/css/tabula.css?"
                            (eval (read-string (env :cache-buster)))))
          "<!--[if lt IE 9]>"
          (include-js "/js/html5shiv.3.7.2.min.js" "/js/respond.1.4.2.min.js")
          "<![endif]-->"]
         [:body body]))
