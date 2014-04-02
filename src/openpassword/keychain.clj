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

