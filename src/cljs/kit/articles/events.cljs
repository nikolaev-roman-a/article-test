(ns kit.articles.events
  (:require
   [re-frame.core :as rf]
   [kit.articles.db :as db]

   [ajax.core :as ajax]))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-fx
 ::navigate
 (fn [_ [_ handler]]
   {:navigate handler}))

(rf/reg-event-fx
 ::set-active-panel
 (fn [{:keys [db]} [_ active-panel]]
   {:db (assoc db :active-panel active-panel)}))

(rf/reg-event-fx
 ::set-route-data
 (fn [{:keys [db]} [_ route]]
   {:db (assoc db :route route)}))


(rf/reg-event-db
 :set-search-query
 (fn [db [_ query]]
   (assoc-in db [:page-data :search :query] query)))

(rf/reg-event-db
 :set-search-response
 (fn [db [_ data]]
   (let [new-page? (and (:page data) (> (parse-long (:page data)) 0))
         current-result (get-in db [:page-data :search :result])
         result (cond
                  (and new-page? (seq (:articles data)))
                  (update data :articles (partial concat (:articles current-result)))

                  (and new-page? (empty? (:articles data)))
                  (do
                    (js/alert "Больше нет")
                    current-result)

                  :else data)]
     (assoc-in db [:page-data :search :result] result))))

(rf/reg-event-db
 :toggle-mobile-menu
 (fn [db _]
   (update db :mobile-menu-open? not)))

(rf/reg-event-fx
 ::search-articles
 (fn [{:keys [db]} [_ {:keys [page]}]]
   (let [query (get-in db [:page-data :search :query] nil)]
     {:http-xhrio {:method          :get
                   :uri             "/api/articles"
                   :timeout         8000
                   :params          (cond-> {}
                                      query
                                      (assoc :query query)
                                      page
                                      (assoc :page page))
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:set-search-response]}})))

(defn inc-page [page]
  (cond (string? page)
        (str (inc (parse-long page)))

        (int? page)
        (inc page)

        (nil? page)
        1))

(rf/reg-event-fx
 ::next-page
 (fn [{:keys [db]} [_ event]]
   (let [current-page (get-in db [:page-data :search :result :page] 0)]
     {:dispatch [event {:page (inc-page current-page)}]})))

(rf/reg-event-fx
 ::get-article
 (fn [{:keys [db]}]
   (let [id (get-in db [:route :route-params :id])]
     {:http-xhrio {:method          :get
                   :uri             (str "/api/articles/" id)
                   :timeout         8000
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:set-search-response]}})))