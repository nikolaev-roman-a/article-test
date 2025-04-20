(ns kit.articles.components
  (:require [re-frame.core :as rf]
            [kit.articles.routes]
            [kit.articles.subs :as subs]))

(defn search-bar [opts]
  [:div.mb-6
   [:label.block.text-sm.font-medium.text-gray-700 {:for "search"} "Поиск"]
   [:div.mt-1.flex.rounded-md.shadow-sm
    [:input#search.w-full.rounded-md.border-gray-300.px-3.py-2.border.focus:ring-blue-500.focus:border-blue-500
     {:type "text"
      :placeholder "Введите запрос..."
      :on-change #(rf/dispatch [:set-search-query (-> % .-target .-value)])
      :on-key-down #(when (= (.-key %) "Enter")
                      ((:on-search opts) %))}]
    [:button.ml-3.inline-flex.items-center.px-4.py-2.border.border-transparent.text-sm.font-medium.rounded-md.shadow-sm.text-white.bg-blue-600.hover:bg-blue-700.focus:outline-none.focus:ring-2.focus:ring-offset-2.focus:ring-blue-500
     {:on-click (:on-search opts)}
     "Найти"]]])

(defn search-results [{:keys [path next-page]} item-view]
  (let [results @(rf/subscribe [:search/results path])]
    [:div.mt-4
     (if (empty? results)
       [:p.text-gray-500 "Ничего не найдено"]
       [:ul.divide-y.divide-gray-200
        (for [item results]
          [:li.py-4
           {:key (:id item)}
           [item-view item]])
        [:li.py-4
         {:key "load-more"}
         [:button.ml-3.inline-flex.items-center.px-4.py-2.border.border-transparent.text-sm.font-medium.rounded-md.shadow-sm.text-white.bg-blue-600.hover:bg-blue-700.focus:outline-none.focus:ring-2.focus:ring-offset-2.focus:ring-blue-500
          {:on-click next-page}
          "Загрузить еще"]]])]))

(defn menu-item [current-route route icon label]
  [:a {:href (kit.articles.routes/url-for route)
       :class (str "flex items-center p-3 rounded-md transition-colors "
                   (if (= current-route route)
                     "bg-blue-100 text-blue-600"
                     "hover:bg-gray-100 text-gray-700"))}
   [:span.material-icons.text-lg icon] ;; Используйте свои иконки
   [:span.ml-3 label]])

(defn sidebar []
  (let [current-route @(rf/subscribe [::subs/active-panel])]
    [:div.fixed.inset-y-0.left-0.w-64.bg-white.shadow-md.z-10
     [:div.p-4.text-xl.font-bold.text-gray-800 "Меню"]
     [:nav.mt-6.px-2
      [menu-item current-route :home "Главная"]
      [menu-item current-route :search "Поиск"]
      #_[menu-item current-route :load "Загрузка"]]]))

(defn mobile-menu []
  (let [open? @(rf/subscribe [:mobile-menu-open?])]
    (when open?
      [:div.md:hidden.fixed.inset-0.z-50
       [:div.absolute.inset-0.bg-gray-600.opacity-75
        {:on-click #(rf/dispatch [:toggle-mobile-menu])}]
       [:div.relative.flex.flex-col.w-80.bg-white.h-full
        [sidebar]]])))

(defn layout [content]
  [:div.flex.flex-col.md:flex-row.h-screen
   [:div.hidden.md:block.w-64.flex-shrink-0
    [sidebar]]

   [:div.flex-1.flex.flex-col.overflow-hidden
    [:header.bg-white.shadow-sm.md:hidden
     [:div.px-4.py-4.flex.items-center
      [:button.p-2.rounded-md.text-gray-500.hover:text-gray-600.hover:bg-gray-100
       {:on-click #(rf/dispatch [:toggle-mobile-menu])}
       [:svg.h-6.w-6 {:fill "none" :viewBox "0 0 24 24" :stroke "currentColor"}
        [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width "2" :d "M4 6h16M4 12h16M4 18h16"}]]]]]

    [:main.flex-1.overflow-y-auto.p-4
     content]

    [mobile-menu]]])