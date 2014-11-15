(ns tabula.model.player
  (:require [datomic.api :refer [q db] :as d]))

(defn add
  [{conn :conn} user-id server-name tribe]
  (d/transact conn
              [{:db/id (Long. user-id)
                :user/player #db/id[:db.part/user -1]}
               {:db/id #db/id[:db.part/user -1]
                :player/tribe (Long. tribe)
                :player/server [:server/name server-name]}]))

(defn tribe
  [{conn :conn} user-id server-name]
  (q '[:find ?tribe .
       :in $ ?u ?server-name
       :where
       [?u :user/player ?p]
       [?p :player/server ?s]
       [?s :server/name ?server-name]
       [?p :player/tribe ?tribe]]
     (db conn)
     (Long. user-id) server-name))
