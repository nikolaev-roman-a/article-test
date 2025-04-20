(ns kit.articles.service.article-source.core
  (:require [integrant.core :as ig]

            [kit.articles.service.article-source.elsevier.core]
            [kit.articles.service.store.core :as store]
            [kit.articles.service.article-source.protocol :as article-source]))

(defn- article-format [data]
  {:title  (:prism:publicationName data)
   :author (:dc:creator data)
   :date   (:prism:coverDate data)
   :doi    (:prism:doi data)})

(defn search-and-save
  [{:keys [query-fn api]}
   {:keys [words]}]
  (let [search-by-word (fn [word]
                         (article-source/search api {:word  word
                                                     :count 10}))
        searchset      (->> (map search-by-word words)
                            (apply concat)
                            (distinct)
                            (map article-format))

        result         (map (partial store/create! query-fn) searchset)]

    result))

(defmethod ig/init-key :article/source [_ context] context)