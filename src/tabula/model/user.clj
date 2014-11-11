(ns tabula.model.user
  (:require [datomic.api :refer [q db] :as d]))

(defn add
  [config {:keys [id first_name locale]}]
  (d/transact (:conn config)
              [{:db/id (Long. id)
                :user/name first_name
                :user/locale locale}]))

(defn get-user
  [config user-id]
  (q '[:find ?l ?n
       :in $ ?id
       :where
       [?id :user/name ?n]
       [?id :user/locale ?l]]
     (db (:conn config))
     (Long. user-id)))
