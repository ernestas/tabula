(ns tabula.view.home
  (:require [tabula.view.page :as page]))

(defn draw
  [request db]
  (page/layout "Home"))
