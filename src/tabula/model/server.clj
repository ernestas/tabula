(ns tabula.model.server
  (:require [datomic.api :refer [q db] :as d]))

(defn server-name
  [{conn :conn} id]
  (q '[:find ?name .
       :in $ ?id
       :where [?id :server/name ?name]]
     (db conn)
     (Long. id)))
