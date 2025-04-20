(ns kit.articles.web.routes.api
  (:require
   [kit.articles.web.controllers.health :as health]
   [kit.articles.web.controllers.articles :as articles]
   [kit.articles.web.controllers.source :as source]
   [kit.articles.web.middleware.exception :as exception]
   [kit.articles.web.middleware.formats :as formats]
   [integrant.core :as ig]
   [reitit.coercion.malli :as malli]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.swagger :as swagger]))

(def route-data
  {:coercion   malli/coercion
   :muuntaja   formats/instance
   :swagger    {:id ::api}
   :middleware [;; query-params & form-params
                parameters/parameters-middleware
                  ;; content-negotiation
                muuntaja/format-negotiate-middleware
                  ;; encoding response body
                muuntaja/format-response-middleware
                  ;; exception handling
                coercion/coerce-exceptions-middleware
                  ;; decoding request body
                muuntaja/format-request-middleware
                  ;; coercing response bodys
                coercion/coerce-response-middleware
                  ;; coercing request parameters
                coercion/coerce-request-middleware
                  ;; exception handling
                exception/wrap-exception]})

;; Routes
(defn api-routes [context]
  [["/swagger.json"
    {:get {:no-doc  true
           :swagger {:info {:title "kit.articles API"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/health"
    {:get #'health/healthcheck!}]

   ["/source/find"
    {:post       {:handler (partial #'source/find-articles context)
                  :parameters {:query {:word string?}}}}]
   ["/articles"
    ["/"
     {:get {:handler    #'articles/search
            :parameters {:query [:map [:page {:optional true} int?]]}}}]
    ["/:id"
     {:post       {:handler #'articles/create}
      :put        {:handler #'articles/save}
      :delete     {:handler #'articles/delete}
      :get        {:handler #'articles/get-data}
      :parameters {:path {:id int?}}}]]])

(derive :reitit.routes/api :reitit/routes)

(defmethod ig/init-key :reitit.routes/api
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  (fn [] [base-path (merge route-data opts) (api-routes opts)]))
