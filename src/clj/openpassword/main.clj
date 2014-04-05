(ns openpassword.main
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Could not find resource"))

(defn -main []
  (jetty/run-jetty app-routes {:port 3000}))
