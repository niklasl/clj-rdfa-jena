//@GrapeResolver(name='clojars.org', root='http://clojars.org/repo')
@Grab('rdfa:rdfa:0.5.1-SNAPSHOT')
@Grab('rdfa:rdfa-jena:0.1.1-SNAPSHOT')
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl
import com.hp.hpl.jena.rdf.model.ModelFactory

RDFReaderFImpl.setBaseReaderClassName("RDFA", "rdfa.adapter.jena.RDFaReader")

def model = ModelFactory.createDefaultModel()

def location = args[0]

// Directly use reader:
//def reader = new rdfa.adapter.jena.RDFaReader()
//reader.read(model, location)
// Or set reader name directly on model:
//model.setReaderClassName("RDFA", "rdfa.adapter.jena.RDFaReader")

// Parse from location:
//model.read(location, "RDFA")
// Parse from input stream:
new URL(location).withInputStream {
    model.read(it, location, "RDFA")
}

//model = new rdfa.adapter.jena.VocabExpander().expand(model)
model.write(System.out, "N3")

