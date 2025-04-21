(ns kit.articles.web.controllers.source
  (:require [clojure.string :as str]

            [ring.util.http-response :as http-response]

            [kit.articles.service.article-source.core :as article-source]))

(defn find-articles
  [context {:keys [params] :as _req}]
  (let [words  (cond (string? (:word params))
                     (some-> (:word params) (str/split #","))

                     (vector? (:word params))
                     (:word params))
        result (->> {:words words}
                    (article-source/search-and-save (:source context)))]
    (http-response/ok {:result result})))