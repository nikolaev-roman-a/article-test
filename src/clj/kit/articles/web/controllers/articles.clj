(ns kit.articles.web.controllers.articles
  (:require [ring.util.http-response :as http-response]

            [kit.articles.web.routes.utils :as utils]
            [kit.articles.service.store.core :as store]))

(defn get-data
  [{:keys [path-params] :as req}]
  (let [{:keys [query-fn]} (utils/route-data req)
        result (store/get-by-id query-fn (:id path-params))]
    (http-response/ok {:article result})))

(def defaults {:count "10"
               :query nil
               :page "0"})

(defn search
  [{:keys [params] :as req}]
  (let [{:keys [query-fn]} (utils/route-data req)

        params*            (reduce-kv (fn [a k v] (if (some? (k a))
                                                    a (assoc a k v)))
                                      params defaults)
        params*            (assoc params* :offset
                                  (* (parse-long (:page params*))
                                     (parse-long (:count params*))))

        result             (store/search query-fn params*)]
    (http-response/ok {:articles result
                       :page     (:page params*)
                       :total    (count result)})))

(defn save
  [{:keys [params] :as req}]
  (let [{:keys [query-fn]} (utils/route-data req)
        result (store/update! query-fn params)]
    (http-response/ok {:result result})))

(defn create
  [{:keys [params] :as req}]
  (let [{:keys [query-fn]} (utils/route-data req)
        result (store/create! query-fn params)]
    (http-response/ok {:article result})))

(defn delete
  [{:keys [path-params] :as req}]
  (let [{:keys [query-fn]} (utils/route-data req)
        result (store/delete! query-fn (:id path-params))]
    
    (http-response/ok {:success (some? result)})))