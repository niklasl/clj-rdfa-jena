(defproject
  rdfa/rdfa-jena "0.1.0-SNAPSHOT"
  :description "Jena adapter for the Clojure RDFa library"
  url "https://github.com/niklasl/clj-rdfa-jena"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [rdfa/rdfa "0.5.0-SNAPSHOT"]
                 [com.hp.hpl.jena/jena "2.6.4"]]
  :aot [rdfa.adapter.jena]
  :ring {:handler rdfa.web/app}
  :main rdfa.stddom)
