(ns tabula.view.server
  (:require [tabula.model.server :as server]
            [tabula.view.page :as page]))

(defn draw
  [server-id request db]
  (page/layout (clojure.string/join " " ["Server" server-id
                                         (server/server-name db server-id)])))
