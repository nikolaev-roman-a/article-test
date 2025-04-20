(ns kit.articles.web.controllers.source
  (:require [clojure.string :as str]

            [ring.util.http-response :as http-response]

            [kit.articles.service.article-source.core :as article-source]))

(defn find-articles
  [context {:keys [params] :as _req}]
  (let [result (->> {:words (some-> (:word params) (str/split #","))}
                    (article-source/search-and-save (:source context)))]
    (http-response/ok {:result result})))