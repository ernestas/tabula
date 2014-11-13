(ns tabula.model.server
  (:require [datomic.api :refer [q db] :as d]))

(defn id
  [{conn :conn} name]
  (q '[:find ?id
       :in $ ?name
       :where [?id :server/name ?name]]
     (db conn)
     name))
