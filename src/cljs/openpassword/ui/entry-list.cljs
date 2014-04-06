(ns openpassword.ui.entry-list
  (:require-macros [hiccups.core :as hiccups])
  (:require [openpassword.ui.eventbus :as eventbus]
            [jayq.core :refer [$ append-to]]))

(defn fetch-and-render-entries []
  )

(defn init []
  (eventbus/listen :vault-unlocked fetch-and-render-entries))
