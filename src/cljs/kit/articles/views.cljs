(ns kit.articles.views
  (:require
   [re-frame.core :as rf]
   [kit.articles.events :as events]
   [kit.articles.routes :as routes]
   [kit.articles.subs :as subs]
   [kit.articles.components :as cmp]))



;; home

(defn home-page []
  [:div.max-w-3xl.mx-auto.p-6
   [:h1.text-3xl.font-bold.text-gray-900.mb-8 "Главная"]
   [:div 
    [:span "Приложение для загрузки и просмотра статей"]]
   [:div
    [:a.inline-flex.items-center.px-4.py-2.border.border-transparent.text-sm.font-medium.rounded-md.shadow-sm.text-white.bg-blue-600.hover:bg-blue-700.focus:outline-none.focus:ring-2.focus:ring-offset-2.focus:ring-blue-500
     {:href "/api/index.html"} "Ссылка на swagger"]]])

(defmethod routes/panels :home-panel [] [home-page])

;; search

(defn article-short-view [item]
  [:a {:key  (:id item)
       :href (str "/articles/" (:id item))}
   [:h3.text-lg.font-medium.text-gray-900 (:title item)]
   [:p.mt-1.text-gray-600 "Автор: " (:author item)]
   [:p.mt-1.text-gray-600 "Дата публикации: " (:date item)]])

(defn search-page []
  [:div.max-w-3xl.mx-auto.p-6
   [:h1.text-3xl.font-bold.text-gray-900.mb-8 "Поиск по базе"]
   [cmp/search-bar {:on-search #(rf/dispatch [::events/search-articles])}]
   [cmp/search-results
    {:path :articles
     :next-page #(rf/dispatch [::events/next-page ::events/search-articles])}
    article-short-view]])

(defmethod routes/panels :search-panel [] [search-page])

;; view

(defn article-page []
  (let [item @(rf/subscribe [:search/results :article])
        ref (str "https://doi.org/" (:doi item))]
    [:div.h-screen.flex.flex-col
     [:div.mx-auto.p-6
      [:h1.text-3xl.font-bold.text-gray-900.mb-8 "Статья: " (:title item)]
      [:div
       [:span.mt-1.text-gray-600 "Автор: " (:author item)]
       [:span.mt-1.text-gray-600.ml-2 "Дата публикации: " (:date item)]
       [:br]
       [:span.mt-1.text-gray-600 "Ссылка: " [:a {:href ref} [:p.underline ref]]]]]
     [:div.flex-1.relative.overflow-hidden
      [:iframe.w-full.h-full.border-0.rounded-lg.shadow-lg
       {:allowFullScreen true
        :style           {:flex "1 1 auto"}
        :src             ref}]]]))

(defmethod routes/panels :item-panel [] [article-page])

;; main

(defn main-panel []
  (let [active-panel (rf/subscribe [::subs/active-panel])]
    [cmp/layout
     (routes/panels @active-panel)]))
