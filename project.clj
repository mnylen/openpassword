(defproject openpassword "0.1.0"
  :description "Open reader for 1Password vaults"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/data.codec "0.1.0"]
                 [cheshire "5.3.1"]]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild {:build []})
