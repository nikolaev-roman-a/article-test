(ns kit.articles.core
  (:require
   [clojure.tools.logging :as log]
   [integrant.core :as ig]
   [kit.articles.config :as config]
   [kit.articles.env :refer [defaults]]

   ;; Edges
   [kit.edge.server.undertow]
   [kit.articles.web.handler]

   ;; Routes
   [kit.articles.web.routes.api]
   [kit.edge.db.sql.conman]
   [kit.edge.db.sql.migratus]
   [kit.articles.web.routes.pages]
   [kit.edge.http.hato])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (fn [thread ex]
   (log/error {:what :uncaught-exception
               :exception ex
               :where (str "Uncaught exception on" (.getName thread))})))

(defonce system (atom nil))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!)))

(defn start-app [& [params]]
  ((or (:start params) (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts params) (:opts defaults) {}))
       (ig/expand)
       (ig/init)
       (reset! system)))

(defn -main [& _]
  (start-app)
  (.addShutdownHook (Runtime/getRuntime) (Thread. (fn [] (stop-app) (shutdown-agents)))))
