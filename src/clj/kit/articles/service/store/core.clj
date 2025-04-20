(ns kit.articles.service.store.core
  (:require [integrant.core :as ig]))

(defmethod ig/init-key :article/store [_ context] context)

(defn get-by-doi [query-fn doi]
  (query-fn :get-article-by-doi {:doi doi}))

(defn get-by-id [query-fn id]
  (query-fn :get-article-by-id {:id id}))

(defn update! [query-fn data]
  (query-fn :update-article! data))

(defn create! [query-fn data]
  (if-let [exists (get-by-doi query-fn (:doi data))]
    (update! query-fn (merge exists data))
    (query-fn :add-article! data)))

(defn delete! [query-fn id]
  (if (get-by-id query-fn id)
    (query-fn :delete-article! {:id id})
    nil))

(defn search [query-fn params]
  (query-fn :search-articles params))