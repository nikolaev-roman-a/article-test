(ns kit.articles.routes
  (:require
   [bidi.bidi :as bidi]
   [pushy.core :as pushy]
   [re-frame.core :as rf]
   [kit.articles.events :as events]))

(defmulti panels identity)
(defmethod panels :default [] [:div "No panel found for this route."])

(def routes
  (atom
   ["/" {""         :home
         "articles" {""        :search
                     ["/" :id] :item}}]))

(def route-events 
  {:search ::events/search-articles
   :item   ::events/get-article})

(defn parse
  [url]
  (bidi/match-route @routes url))

(defn url-for
  [& args]
  (apply bidi/path-for (into [@routes] args)))

(defn dispatch
  [route]
  (let [h (:handler route)
        panel (keyword (str (name h) "-panel"))
        event (get route-events h)]
    (rf/dispatch [::events/set-active-panel panel])
    (rf/dispatch [::events/set-route-data route])
    (rf/dispatch [event])))

(defonce history
  (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (pushy/set-token! history (url-for handler)))

(defn start!
  []
  (pushy/start! history))

(rf/reg-fx
 :navigate
 (fn [handler]
   (navigate! handler)))

(rf/reg-event-fx
 :redirect
 (fn [url]
   (pushy/set-token! history url)))
