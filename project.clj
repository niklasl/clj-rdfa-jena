(defproject
  rdfa/rdfa-jena "0.1.1-SNAPSHOT"
  :description "Jena adapter for the Clojure RDFa library"
  :url "https://github.com/niklasl/clj-rdfa-jena"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [rdfa/rdfa "0.5.1-SNAPSHOT"]
                 [org.apache.jena/jena-core "2.7.3"]]
  :aot [rdfa.adapter.jena]
  :target-dir "target"
  :jar-exclusions [#"(?:^|/)\..+"])
