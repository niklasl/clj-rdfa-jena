//@GrapeResolver(name='clojars.org', root='http://clojars.org/repo')
@Grab('rdfa:rdfa:0.5.0-SNAPSHOT')
@Grab('rdfa:rdfa-jena:0.1.0-SNAPSHOT')

def path = args[0]

def model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel()

//def reader = new rdfa.adapter.jena.RDFaReader()
//reader.read(model, path)

model.setReaderClassName("RDFA", "rdfa.adapter.jena.RDFaReader")
//model.read(path, "RDFA")
new File(path).withInputStream { model.read(it, path, "RDFA") }

model.write(System.out, "N3")

