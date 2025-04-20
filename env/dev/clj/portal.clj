(ns portal
  (:require [portal.api :as p]))

(def instance
  "Open portal window if no portal sessions have been created.
   A portal session is created when opening a portal window"
  (or (= (System/getenv "NO_PORTAL") "true")
      (seq (p/sessions))
      (p/open {:launcher :vs-code
               :portal.colors/theme :portal.colors/gruvbox})))

(add-tap #'p/submit)

