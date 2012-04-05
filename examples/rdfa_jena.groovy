@Grab('xerces:xercesImpl:2.7.1')
@Grab('com.hp.hpl.jena:jena:2.6.4')
@Grab('net.sourceforge.nekohtml:nekohtml:1.9.15')
//@GrapeResolver(name='clojars.org', root='http://clojars.org/repo')
@Grab('rdfa:rdfa:0.5.0-SNAPSHOT')
@Grab('rdfa:rdfa-jena:0.1.0-SNAPSHOT')

import com.hp.hpl.jena.rdf.model.ModelFactory

def path = args[0]

def model = ModelFactory.createDefaultModel()

//def reader = new rdfa.adapter.jena.RDFaReader()
//reader.read(model, path)

model.setReaderClassName("RDFA", "rdfa.adapter.jena.RDFaReader")
model.read(path, "RDFA")

//InputStream ins = FileManager.get().open(path)
//model.read(ins, "RDFA")

model.write(System.out, "N3")

