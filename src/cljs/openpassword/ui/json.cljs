(ns openpassword.ui.json)

(defn parse [data]
  (js->clj data :keywordize-keys true))

(defn stringify [data]
  (.stringify js/JSON (clj->js data)))

