(ns openpassword.encryption 
  (:require [clojure.data.codec.base64 :as base64]
            [cheshire.core :as json]))

(import javax.crypto.SecretKeyFactory)
(import javax.crypto.Cipher)
(import javax.crypto.spec.PBEKeySpec)
(import javax.crypto.spec.SecretKeySpec)
(import javax.crypto.spec.IvParameterSpec)
(import java.security.MessageDigest)


(def salt-prefix [83 97 108 116 101 100 95 95])
(def zero-iv [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]) 

(defn is-salted? [data]
  (loop [remaining-prefix salt-prefix
         remaining-data   data]

    (let [prefix-next (first remaining-prefix)
          data-next   (first remaining-data)]

      (if (nil? prefix-next)
        true
        (if (= prefix-next data-next)
          (recur (rest remaining-prefix) (rest remaining-data))
          false)))))

(defn get-salt-and-binary [data]
  (if (is-salted? data)
    [(take 8 (drop 8 data)) (drop 16 data)]
    [zero-iv data]))

(defn pbkdf2 [password salt iterations]
  (let [password-chars (.toCharArray password)
        salt-bytes     (byte-array salt)
        key-spec       (PBEKeySpec. password-chars salt-bytes iterations (* 8 32))
        key-factory    (SecretKeyFactory/getInstance "PBKDF2WithHmacSHA1")]

    (.getEncoded (.generateSecret key-factory key-spec))))

(defn aes-decrypt [binary key iv]
  (let [cipher (Cipher/getInstance "AES/CBC/NoPadding" "SunJCE")
        key-spec  (SecretKeySpec. (byte-array key) "AES")
        iv-spec   (IvParameterSpec. (byte-array iv))]

   (.init cipher Cipher/DECRYPT_MODE key-spec iv-spec)
   (drop-last 16 (.doFinal cipher (byte-array binary))))) ; drop the last block

(defn decrypt-encryption-key [data password iterations]  
  (let [[salt binary] (get-salt-and-binary data)
        derived-key   (pbkdf2 password salt iterations)
        key     (take 16 derived-key) 
        iv      (drop 16 derived-key)
        final-key (aes-decrypt binary key iv)]
    (drop 0 final-key)))

(defn md5 [data]
  (let [md (MessageDigest/getInstance "MD5")]
    (.digest md (byte-array data))))

(defn derive-openssl-key-and-iv [password salt]
  (let [data (concat password salt) rounds 2]
    (loop [round     1
           prev-hash (md5 data)
           result    prev-hash]

      (if (= round rounds)
        [(take 16 result) (take 32 (drop 16 result))]
        (let [next-hash (md5 (concat prev-hash data))]
          (recur (inc round) next-hash (concat result next-hash))))))) 

(defn decrypt-verification [data decrypted-key]
  (if (not (is-salted? data))
    (aes-decrypt data (md5 decrypted-key) zero-iv)
    (do
      (let [[salt binary] (get-salt-and-binary data)
            [key iv] (derive-openssl-key-and-iv decrypted-key salt)]
        (aes-decrypt binary key iv)))))



(defn load-encryption-keys [root-path]
  (let [data (slurp (clojure.string/join "/" [root-path "data" "default" "encryptionKeys.js"]))]
    (json/parse-string data true)))

(defn find-sl5-item [encryption-keys]
  (let [item-id (:SL5 encryption-keys)]
    (first (filter #(= item-id (:identifier %)) (:list encryption-keys)))))

(defn decode-as-byte-array [data]
  (base64/decode (.getBytes data "UTF-8")))

(defn get-master-key [encryption-keys password]
  (let [item           (find-sl5-item encryption-keys)
        iterations     (max 1000 (int (or (:iterations item) 0)))
        encryption-key (decrypt-encryption-key (decode-as-byte-array (:data item)) password iterations)
        verification   (decrypt-verification (decode-as-byte-array (:validation item)) encryption-key)]

     (if (= encryption-key verification)
       encryption-key
       nil)))

