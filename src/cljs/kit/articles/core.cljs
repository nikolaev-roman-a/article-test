(ns kit.articles.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [day8.re-frame.http-fx]
   [kit.articles.events :as events]
   [kit.articles.routes :as routes]
   [kit.articles.views :as views]
   [kit.articles.config :as config]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn ^:export ^:dev/once init! []
  (routes/start!)
  (rf/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))