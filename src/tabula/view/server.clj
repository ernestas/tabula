(ns tabula.view.server
  (:require [tabula.model.server :as server]
            [tabula.view.page :as page]))

(defn draw
  [server-name request db]
  (page/layout (clojure.string/join " " ["Server" server-name
                                         (server/id db server-name)])))
