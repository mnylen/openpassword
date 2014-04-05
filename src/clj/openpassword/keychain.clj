(ns openpassword.keychain
  (:require [openpassword.encryption :as encryption]
            [cheshire.core :as json]))

(defn load-data-file [root-path fname]
  (let [full-name (clojure.string/join "/" [root-path "data" "default" fname])
        contents  (slurp full-name)]
    (json/parse-string contents true)))

(defn open [root-path password]
  (let [encryption-keys (load-data-file root-path "encryptionKeys.js")]
    (if-let [master-key (encryption/get-master-key encryption-keys password)]
      {:root-path root-path :master-key master-key}
      nil)))

(defn list-entries [kc]
  (let [contents (load-data-file (:root-path kc) "contents.js")]
    (mapv #(hash-map :uuid       (nth % 0)
                     :entry-type (nth % 1)
                     :title      (nth % 2)
                     :url        (nth % 3)) contents))) 


