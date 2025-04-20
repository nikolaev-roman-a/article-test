(ns kit.articles.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[articles starting]=-"))
   :start      (fn []
                 (log/info "\n-=[articles started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[articles has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})
