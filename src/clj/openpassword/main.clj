(ns openpassword.main
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.reload :refer [wrap-reload]]
            [cheshire.core :as json]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [clojure.pprint :refer [pprint]]
            [openpassword.keychain :as keychain]))

(defn do-login [request-body session]
  (let [master-password (:password (json/parse-string request-body true))]
    (if-let [kc (keychain/open "resources/TestVault.agilekeychain" master-password)]
      {:status 200 :session (assoc session :keychain kc)}
      {:status 400 :session session})))

(defroutes app-routes
  (POST "/login" {body :body session :session} (do-login (slurp body) session))
  (route/resources "/")
  (route/not-found "Could not find resource"))

(def app
  (-> app-routes
      (wrap-reload ['openpassword.main 'openpassword.keychain])
      wrap-session))

(def server (atom nil))

(defn run [background?]
  (reset! server (if background?
                   (jetty/run-jetty app {:port 3000 :join? false})
                   (jetty/run-jetty app {:port 3000 :join? true}))))

(defn restart []
  (when @server (.stop @server))
  (run true))

(defn -main []
  (run))
