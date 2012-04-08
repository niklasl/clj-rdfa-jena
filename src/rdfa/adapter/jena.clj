(ns rdfa.adapter.jena
  (:import [rdfa.core IRI Literal BNode])
  (:use [rdfa.core :only [rdfa:usesVocabulary]])
  (:require [rdfa.parser])
  (:import [com.hp.hpl.jena.rdf.model ModelFactory]
           [com.hp.hpl.jena.reasoner ReasonerRegistry]))


(defn create-node [model term]
  (condp = (type term)
    IRI (.createResource model (:id term))
    Literal (let [{value :value tag :tag} term]
              (cond
                (= (type tag) IRI)
                (.createTypedLiteral model value (:id tag))
                (not-empty tag)
                (.createLiteral model value tag)
                :else
                (.createLiteral model value)))
    BNode (.createResource model
                           (com.hp.hpl.jena.rdf.model.AnonId. (:id term)))))

(defn triples-into-model [triples model]
  (doseq [[s p o] triples]
    (.add model
          (create-node model s)
          (.createProperty model (:id p))
          (create-node model o))))

(defn read-into-model [model & args]
  (let [{:keys [env triples proc-triples]} (apply rdfa.parser/get-rdfa args)]
    (.setNsPrefixes model (:prefix-map env))
    ;(.setNsPrefix model (:vocab env))
    (triples-into-model triples model)))

(defn triples-to-model [triples]
  (let [model (ModelFactory/createDefaultModel)]
    (triples-into-model triples model)
    model))

(defn load-vocab [vocab-paths cache]
  (let [voc-model (ModelFactory/createDefaultModel)]
    (doseq [vocab-path vocab-paths]
      ; TODO: send mime-type and encoding to rdfa.parser
      (let [url (java.net.URL. vocab-path)
            uc (.openConnection url)
            content-encoding (.getContentEncoding uc)
            content-type (.getContentType uc)]
        (with-open [stream (.getInputStream uc)]
          (if (or (.equals content-type "text/html")
                  (.equals content-type "application/xhtml+xml")
                  (.endsWith vocab-path "html"))
            (read-into-model voc-model stream vocab-path)
            (.read voc-model stream vocab-path)))))
    voc-model))

(defn find-vocab-paths [model]
  (map #(.. % (asResource) (getURI))
       (iterator-seq
         (.listObjectsOfProperty model (create-node model rdfa:usesVocabulary)))))

(defn expand-vocab [model & {:keys [cache]}]
  (let [voc-model (load-vocab (find-vocab-paths model) cache)
        reasoner (.bindSchema (ReasonerRegistry/getOWLMicroReasoner) voc-model)
        inf-model (ModelFactory/createInfModel reasoner model)
        ; TODO: find a lean solution to get only the inferred triples
        inf-voc-model (ModelFactory/createInfModel reasoner (ModelFactory/createDefaultModel))
        res-model (.difference inf-model inf-voc-model)]
    (.setNsPrefixes res-model (.getNsPrefixMap model))
    res-model))


(gen-class
  :name rdfa.adapter.jena.RDFaReader
  :implements [com.hp.hpl.jena.rdf.model.RDFReader]
  :state state
  :init init
  :prefix "parser-")

(defn parser-init []
  [[] (atom {})])

(defn parser-setErrorHandler [this err-handler]
  (swap! (.state this) assoc :error-handler err-handler))

(defn parser-setProperty [this pname value]
  (swap! (.state this) assoc pname value))

(defn parser-read
  ;parser-read-Model-(InputStream/Reader)-String
  ([this model source base]
   (read-into-model model source base))
  ;parser-read-Model-String
  ([this model url]
   (read-into-model model url)))


(gen-class
  :name rdfa.adapter.jena.VocabExpander
  :prefix "expander-"
  :methods [[expand [com.hp.hpl.jena.rdf.model.Model] com.hp.hpl.jena.rdf.model.Model]])

(defn expander-expand [this model]
  (expand-vocab model))

