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

(defn open-keychain [request-body session]
  (let [master-password (:password (json/parse-string request-body true))]
    (if-let [kc (keychain/open "resources/TestVault.agilekeychain" master-password)]
      {:status 200 :session (assoc session :keychain kc)}
      {:status 400 :session session})))

(defn list-keychain-entries [session]
  (if-let [kc (:keychain session)]
    {:status 200, :body (json/generate-string {:data (keychain/list-entries kc)})}
    {:status 401}))

(defn keychain-status [session]
  (if (:keychain session)
    {:status 200 :body (json/generate-string {:data {:open true}})}
    {:status 200 :body (json/generate-string {:data {:open false}})}))

(defroutes app-routes
  (GET "/keychain/entries" {session :session} (list-keychain-entries session))
  (GET "/keychain/status" {session :session} (keychain-status session))
  (POST "/keychain/open" {body :body session :session} (open-keychain (slurp body) session))
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
