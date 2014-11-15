(ns tabula.model.player
  (:require [datomic.api :refer [q db] :as d]))

(defn add
  [{conn :conn} user-id server-name tribe]
  (d/transact conn
              [{:db/id [:user/id user-id]
                :user/player #db/id[:db.part/user -1]}
               {:db/id #db/id[:db.part/user -1]
                :player/tribe (Long. tribe)
                :player/server [:server/name server-name]}]))

(defn tribe
  [{conn :conn} user-id server-name]
  (q '[:find ?tribe .
       :in $ ?user-id ?server-name
       :where
       [?u :user/id ?user-id]
       [?u :user/player ?p]
       [?p :player/server ?s]
       [?s :server/name ?server-name]
       [?p :player/tribe ?tribe]]
     (db conn)
     user-id server-name))
