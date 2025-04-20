(ns kit.articles.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 ::name
 (fn [db]
   (:name db)))

(rf/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(rf/reg-sub
 :search/results
 (fn [db [_ path]]
   (get-in db [:page-data :search :result path])))

(rf/reg-sub
 :mobile-menu-open?
 (fn [db] (:mobile-menu-open? db)))

(rf/reg-sub
 :article-data
 (fn [db] (get-in db [:page-data :article])))