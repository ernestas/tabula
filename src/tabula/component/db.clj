(ns tabula.component.db
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]))

(defrecord Db [uri]
  component/Lifecycle
  (start [this]
    (if (:conn this)
      this
      (assoc this :conn (d/connect uri))))
  (stop [this]
    (if (:conn this)
      (do (d/release (:conn this))
          (assoc this :conn nil))
      this)))

(defn database-component [uri]
  (->Db uri))
