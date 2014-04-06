(ns openpassword.ui.entry-list
  (:require-macros [hiccups.core :as hiccups])
  (:require [openpassword.ui.eventbus :as eventbus]
            [openpassword.ui.json :as json]
            [jayq.core :refer [$ append-to ajax find]]
            [hiccups.runtime :as hiccupsrt]))

(hiccups/defhtml entry-template [entry]
  [:li
   [:a {:href "#"}
    (:title entry)]])

(hiccups/defhtml main-view []
  [:div {:id "main-view"}
   [:input {:type "search" :autofocus true :placeholder "Search"}]
   [:ul {:id "entries"}]])

(defn render-entries [entries]
  (let [$view ($ (main-view))
        $ul   (find $view "ul")]
    (append-to $view "body")
    (doseq [entry entries]
      (let [$li ($ (entry-template entry))]
        (append-to $li $ul)))))


(defn fetch-and-render-entries []
  (let [response (ajax {:url "/keychain/entries" :dataType "json"})]
    (.success response #(render-entries (:data (json/parse %))))
    (.error response #(.log js/console "Error occured: " %))))

(defn init []
  (eventbus/listen :vault-unlocked fetch-and-render-entries))
