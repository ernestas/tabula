(ns tabula.model.user
  (:require [datomic.api :refer [q db] :as d]))

(defn add
  [{conn :conn} {:keys [id first_name locale]}]
  (d/transact conn
              [{:db/id #db/id[:db.part/user]
                :user/id id
                :user/name first_name
                :user/locale locale}]))

(defn get-user
  [{conn :conn} user-id]
  (q '[:find [?u ?l ?n]
       :in $ ?user-id
       :where
       [?u :user/id ?user-id]
       [?u :user/name ?n]
       [?u :user/locale ?l]]
     (db conn)
     user-id))
