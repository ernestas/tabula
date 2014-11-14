(ns tabula.model.player
  (:require [datomic.api :refer [q db] :as d]))

(defn add
  [{conn :conn} user-id server-id tribe]
  (d/transact conn
              [{:db/id (Long. user-id)
                :user/player #db/id[:db.part/user -1]}
               {:db/id #db/id[:db.part/user -1]
                :player/tribe (Long. tribe)
                :player/server (Long. server-id)}]))

(defn tribe
  [{conn :conn} user-id server-id]
  (q '[:find ?tribe .
       :in $ ?u ?s
       :where
       [?u :user/player ?p]
       [?p :player/tribe ?tribe]
       [?p :player/server ?s]]
     (db conn)
     (Long. user-id) (Long. server-id)))
