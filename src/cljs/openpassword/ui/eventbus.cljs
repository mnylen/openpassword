(ns openpassword.ui.eventbus)

(def listeners (atom {}))

(defn listeners-for-type [event-type]
  (or (event-type @listeners) []))

(defn listen [event-type listener-fn]
  (swap! listeners assoc event-type (conj (listeners-for-type event-type) listener-fn)))

(defn trigger
  ([event-type data]
    (doseq [listener-fn (listeners-for-type event-type)]
      (listener-fn data)))
  ([event-type]
    (trigger event-type {})))



