(ns kit.articles.service.article-source.elsevier.core
  (:require [clojure.string :as str]

            [cheshire.core :as json]
            [integrant.core :as ig]
            [hato.client :as hc]

            [kit.articles.service.article-source.protocol :refer [ArticleSource]]))

(defrecord ElsevierApi [context]
  ArticleSource
  (search
    [{:keys  [apikey search-host]
      client :http/client}
     {:keys [word count]}]
    (let [response (->> {:http-client  client
                         :query-params {:query  word
                                        :apiKey apikey
                                        :count count}}
                        (hc/get search-host))]
      (-> (:body response)
          (json/decode keyword)
          :search-results
          :entry))))

(defmethod ig/init-key :elsevier/api
  [_ context]
  (map->ElsevierApi context))

