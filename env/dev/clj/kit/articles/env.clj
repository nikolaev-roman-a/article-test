(ns kit.articles.env
  (:require
    [clojure.tools.logging :as log]
    [kit.articles.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[articles starting using the development or test profile]=-"))
   :start      (fn []
                 (log/info "\n-=[articles started successfully using the development or test profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[articles has shut down successfully]=-"))
   :middleware wrap-dev
   :opts       {:profile       :dev}})
