(ns openpassword.ui.unlock-vault
  (:require-macros [hiccups.core :as hiccups]
                   [jayq.macros :refer [let-ajax]])
  (:require [hiccups.runtime :as hiccupsrt]
            [jayq.core :refer [$ bind append-to ajax val find add-class]]
            [openpassword.ui.eventbus :as eventbus]
            [openpassword.ui.json :as json]))

(hiccups/defhtml form-template []
  [:div {:id "login-form"}
   [:span "Unlock vault"]
   [:div
    [:input {:type "password" :autofocus true :placeholder "Master password"}]
    [:button "Unlock"]
    [:span {:class "failed-login hidden"}
     "Incorrect password."]]])

(def keycode-enter 13)

(defn bind-enter-press [$el action-fn]
  (bind $el "keydown" (fn [e]
                        (when (= keycode-enter (. e -keyCode))
                          (.preventDefault e)
                          (action-fn)))))


(defn unlock-request [password]
  {:url "/keychain/open"
   :method "POST"
   :data (json/stringify {:password password})})

(defn password-input [$form]
  (find $form "input[type=password]"))

(defn on-success [$form]
  (.addClass (find $form "span.failed-login") "hidden")
  (.fadeOut $form 1500 #(.remove $form))
  (eventbus/trigger :vault-unlocked))

(defn on-error [$form]
  (let [$notification-text (find $form "span.failed-login")]
    (.log js/console $notification-text)
    (.removeClass $notification-text "hidden")
    (.focus (password-input $form))))

(defn try-login [$form]
  (let [password (val (password-input $form))] 
    (if (empty? password)
      (on-error $form)
      (let [response (ajax (unlock-request password))]
        (.success response #(on-success $form))
        (.error response #(on-error $form))))))


(defn get-status [callback]
  (let [response (ajax {:url "/keychain/status" :method "GET" :dataType "json"})]
    (.success response #(callback (json/parse %)))
    (.error response #(.log js/console %))))

(defn render []
  (let [$form ($ (form-template))]
    (append-to $form :body)
    (let [$unlock-button  (find $form "button")]
      (bind $unlock-button "click" #(try-login $form))
      (bind-enter-press (password-input $form) #(try-login $form)))))

(defn unlock-vault []
  (get-status #(let [open? (-> % :data :open)]
                 (if open?
                   (eventbus/trigger :vault-unlocked)
                   (render)))))

(defn init []
  (eventbus/listen :unlock-vault unlock-vault))
