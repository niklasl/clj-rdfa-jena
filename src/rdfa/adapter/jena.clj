(ns rdfa.adapter.jena
  (:import [rdfa.core IRI Literal BNode])
  (:require [rdfa.stddom :as impl]))


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


(gen-class
  :name rdfa.adapter.jena.RDFaReader
  :implements [com.hp.hpl.jena.rdf.model.RDFReader]
  :prefix "parser-")

(defn parser-setErrorHandler [this err-handler]
  err-handler)

(defn parser-setProperty [this pname value]
  nil)

(defn parser-read-Model-InputStream-String
  [this model in-stream base]
  (throw (UnsupportedOperationException.)))

(defn parser-read-Model-Reader-String
  [this model reader base]
  (throw (UnsupportedOperationException.)))

(defn parser-read-Model-String
  [this model url]
  (let [{:keys [env triples proc-triples]} (impl/get-rdfa url)]
    (doseq [[s p o] triples]
      (.add model
            (create-node model s)
            (.createProperty model (:id p))
            (create-node model o)))))


(defn expand-vocabulary [model]
  nil)

(gen-class
  :name rdfa.adapter.jena.VocabExpander
  :prefix "expander-"
  :methods [[expand [com.hp.hpl.jena.rdf.model.Model] com.hp.hpl.jena.rdf.model.Model]])

(defn expander-expand [this model]
  (expand-vocabulary model))

