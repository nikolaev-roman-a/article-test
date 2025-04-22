(ns kit.articles.service.article-source.core
  (:require [integrant.core :as ig]

            [kit.articles.service.article-source.elsevier.core]
            [kit.articles.service.store.core :as store]
            [kit.articles.service.article-source.protocol :as article-source]))

(defn search-and-save
  [{:keys [query-fn api]}
   {:keys [words]}]
  (letfn
   [(search-by-word [word]
      (article-source/search api {:word  word
                                  :count 10}))]

    (->> (mapcat search-by-word words)
         (map (partial store/create! query-fn)))))

(defmethod ig/init-key :article/source [_ context] context)