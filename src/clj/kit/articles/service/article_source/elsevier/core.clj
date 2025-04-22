(ns kit.articles.service.article-source.elsevier.core
  (:require [clojure.string :as str]

            [cheshire.core :as json]
            [integrant.core :as ig]
            [hato.client :as hc]

            [kit.articles.service.article-source.protocol :refer [ArticleSource]]))

(defn- article-format [data]
  {:title  (:prism:publicationName data)
   :author (:dc:creator data)
   :date   (:prism:coverDate data)
   :doi    (:prism:doi data)})

(defn- make-request-ex [e]
  (let [data  (ex-data e)
        error (-> data :body (json/decode keyword) :service-error)]
    (ex-info "ElsevierApiError" {:type  :system.exception/internal
                                 :error error
                                 :request (select-keys (:request data) [:url :query-string])})))
 
(defrecord ElsevierApi [context]
  ArticleSource
  (search
   [{:keys  [apikey apiurl]
     client :http/client}
    {:keys [word count]}]
   (let [response   (try (->> {:http-client  client
                               :query-params {:query  word
                                              :apiKey apikey
                                              :count  count}}
                              (hc/get apiurl))
                         (catch Exception e
                           (throw (make-request-ex e))))

         result-set (-> (:body response)
                        (json/decode keyword)
                        :search-results
                        :entry)]
     (when-not (:error (first result-set))
       (map article-format result-set)))))

(defmethod ig/init-key :elsevier/api
  [_ context]
  (map->ElsevierApi context))