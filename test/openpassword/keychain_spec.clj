(ns openpassword.keychain-spec
  (:require [clojure.test :refer :all]
            [openpassword.keychain :as keychain]))

(def keychain-root-path "resources/TestVault.agilekeychain")

(deftest opening-keychain
  (testing "should return keychain when opening with correct master password"
    (let [kc (keychain/open keychain-root-path "v3rys3kret")]
      (is (not (nil? kc)))
      (is (= keychain-root-path (:root-path kc)))
      (is (not (nil? (:master-key kc))))))

  (testing "should return nil when opening with incorrect master password"
    (let [kc (keychain/open keychain-root-path "wrongp4ssw0rd")]
      (is (nil? kc)))))

(defn contains-entry? [entries entry]
  (some (fn [candidate]
          (if (= candidate entry)
            true
            false)) entries))

(deftest test-keychain-list-entries
  (testing "should list all entries in keychain"
    (let [kc (keychain/open keychain-root-path "v3rys3kret") entries (keychain/list-entries kc)]
      (is (= entries [{:uuid       "7AE0E2496FA74055A9694429F7F8EF73"
                       :title      "Facebook"
                       :entry-type "webforms.WebForm"
                       :url        "http://facebook.com/"}
                      {:uuid       "F44BF45E686C41589EDDED48D22D7E35"
                       :title      "Twitter"
                       :entry-type "webforms.WebForm"
                       :url        "http://www.twitter.com"}
                      {:uuid       "0919D91FFA074B90A1081ED721005AED"
                       :title      "LinkedIn"
                       :entry-type "webforms.WebForm"
                       :url        "http://www.linkedin.com/"}
                      {:uuid       "A3FBAA0A2A6F406A89A0B9AD1E142CC8"
                       :title      "Google"
                       :entry-type "webforms.WebForm"
                       :url        "http://google.com"}])))))
 
