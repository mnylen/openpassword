(defproject openpassword "0.1.0"
  :description "Open reader for 1Password vaults"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.codec "0.1.0"]
                 [cheshire "5.3.1"]
                 [ring "1.2.2"]
                 [compojure "1.1.6"]

                 ; UI 
                 [org.clojure/clojurescript "0.0-2202"]
                 [hiccups "0.3.0"]
                 [jayq "2.5.0"]]
  :source-paths ["src/clj"]
  :aot [openpassword.main]
  :main openpassword.main
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild {:builds [{:source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/application.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
