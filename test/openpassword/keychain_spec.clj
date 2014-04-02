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

