(ns kit.articles.db
  (:require [re-frame.core :as rf]))

(def default-db
  {:active-panel nil
   :page-params  {:query ""
                  :page  1}
   :page-data    {}
   :loading?     false
   :error        nil})

(rf/reg-event-db
 :initialize-db
 (fn [db _]
   (merge default-db db)))