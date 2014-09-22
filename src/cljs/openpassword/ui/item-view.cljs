(ns openpassword.ui.item-view
  (:require-macros [hiccups.core :as hiccups])
  (:require [openpassword.ui.eventbus :as eventbus]
            [jayq.core :refer [$ html bind append-to remove-class]]))

(hiccups/defhtml item-view []
  [:div {:id "item-view" :class "hidden"}])

(hiccups/defhtml item-template [item]
  [:div 
   [:h3 (:title item)]
   [:a {:href (:url item) :target "_blank"} (:url item)]
   [:ul {:id "properties"}]]) 

(defn create-item-view []
  (append-to ($ (item-view)) ($ "body")))

(defn open-item [item]
  (let [$container ($ "#item-view")]
    (html $container (item-template item))
    (remove-class $container "hidden")))

(defn init []
  (create-item-view)
  (eventbus/listen :item-opened #(open-item (:item %))))
