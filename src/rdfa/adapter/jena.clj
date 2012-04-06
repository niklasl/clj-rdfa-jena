(ns rdfa.adapter.jena
  (:import [rdfa.core IRI Literal BNode])
  (:require [rdfa.parser]))


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

(defn read-into-model [model source base]
  (let [{:keys [env triples proc-triples]} (rdfa.parser/get-rdfa source base)]
    (doseq [[s p o] triples]
      (.add model
            (create-node model s)
            (.createProperty model (:id p))
            (create-node model o)))))


(defn expand-vocabulary [model]
  ; TODO:
  ; each vocab parse-into-vocab-model
  ; expand
  model)


(gen-class
  :name rdfa.adapter.jena.RDFaReader
  :implements [com.hp.hpl.jena.rdf.model.RDFReader]
  :prefix "parser-")

(defn parser-setErrorHandler [this err-handler]
  err-handler)

(defn parser-setProperty [this pname value]
  nil)

(defn parser-read
  ;parser-read-Model-InputStream-String
  ;parser-read-Model-Reader-String
  ([this model source base]
   (read-into-model model source base))
  ;parser-read-Model-String
  ([this model url]
   (read-into-model model url url)))


(gen-class
  :name rdfa.adapter.jena.VocabExpander
  :prefix "expander-"
  :methods [[expand [com.hp.hpl.jena.rdf.model.Model] com.hp.hpl.jena.rdf.model.Model]])

(defn expander-expand [this model]
  (expand-vocabulary model))

