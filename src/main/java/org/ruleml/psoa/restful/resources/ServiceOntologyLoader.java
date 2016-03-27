package org.ruleml.psoa.restful.resources;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Set;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.util.URLUtils;
import org.ruleml.psoa.restful.models.URILoadRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.owl.owlapi.tutorial.LabelExtractor;

/**
 * Created by sadnana on 26/02/16.
 */
@Path("/loadserviceont")
public class ServiceOntologyLoader {

    private static int INDENT = 4;
    Logger logger = Logger.getLogger(ServiceOntologyLoader.class);
    @Context
    UriInfo info;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded
    public String loadServiceOntology(URILoadRequest uriLoadRequest) throws OWLOntologyCreationException, OWLException,
            InstantiationException, IllegalAccessException,
            ClassNotFoundException, UnsupportedEncodingException, URISyntaxException, MalformedURLException {

        logger.info("Loading Service Ontology...");

        String reasonerFactoryClassName = null;

        URI urlabsolute = null;
        urlabsolute = new URI(uriLoadRequest.getIri());
        logger.info("ABSOLUTE URI "+ urlabsolute);
        /* Get an Ontology Manager */
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        logger.info("MANAGER ERROR...");
        URLEncoder.encode(uriLoadRequest.getIri(), "UTF-8");
        logger.info("ENCODING ERROR...");
        //IRI inputDocumentIRI = IRI.create();
        IRI inputDocumentIRI = IRI.create(decode(uriLoadRequest.getIri()));

        logger.info("IRI CREATION ERROR...");
        logger.info("IRI "+ inputDocumentIRI.toString());


        /* Load an ontology from a document IRI */

        OWLOntology ontology;
        ontology = manager.loadOntology(inputDocumentIRI);

        logger.info("LoaDED...");
        /* Report information about the ontology */
        System.out.println("Ontology Loaded...");
        System.out.println("Document IRI: " + inputDocumentIRI);
        System.out.println("Logical IRI : " + ontology.getOntologyID());
        System.out.println("Format      : "
                + manager.getOntologyFormat(ontology));

        String classesInDomOnt = "";
        classesInDomOnt += "-------- classes --------" + "\n";
        Set<OWLClass> classes = ontology.getClassesInSignature();
        for(OWLClass clz :  classes) {
            classesInDomOnt += clz.toStringID() + "\n";
            System.out.println("class -: " + clz.toStringID());
            //classesInDomOnt += "document.write(\"<a href=\""+ clz.toStringID() + "\">" + clz.getIRI().getFragment() + "</a>\")";
        }

        String objPropInDomOnt = "";
        classesInDomOnt += "-------- Object Properties --------" + "\n";
        Set<OWLObjectProperty> objProperties = ontology.getObjectPropertiesInSignature();
        for(OWLObjectProperty op :  objProperties) {
            classesInDomOnt += op.toStringID() + "\n";
            System.out.println("Object property -: " + op.toStringID());
            //classesInDomOnt += "document.write(\"<a href=\""+ clz.toStringID() + "\">" + clz.getIRI().getFragment() + "</a>\")";
        }


        String dataPropInDomOnt = "";
        dataPropInDomOnt += "-------- Data Properties --------" + "\n";
        Set<OWLDataProperty> dataProperties = ontology.getDataPropertiesInSignature();
        for(OWLObjectProperty dp :  objProperties) {
            dataPropInDomOnt += dp.toStringID() + "\n";
            System.out.println("Data property -: " + dp.toStringID());
            //classesInDomOnt += "document.write(\"<a href=\""+ clz.toStringID() + "\">" + clz.getIRI().getFragment() + "</a>\")";
        }

        String clsOpDpInDomain = classesInDomOnt + objPropInDomOnt + dataPropInDomOnt;

        manager.removeOntology(ontology);


        // / Create a new SimpleHierarchy object with the given reasoner.
        /*
        DomainOntologyLoader domainOntologyLoader = new DomainOntologyLoader(
                (OWLReasonerFactory) Class.forName(reasonerFactoryClassName)
                        .newInstance(), ontology);
        */
        // Get Thing
        OWLClass clazz = manager.getOWLDataFactory().getOWLThing();
        System.out.println("Class       : " + clazz);
        // Print the hierarchy below thing
        //String ontologyHierarchy = printHierarchy(clazz, (OWLReasonerFactory) Class.forName(reasonerFactoryClassName)
        //      .newInstance(), ontology, System.out);


        //String kb = decode(req.getDocument());

        //String query = decode(req.getQuery());
        /* only returning the kb for testing if the client can receive
         * it , hence the try/catch block is disabled here
         */
        /*
         try {
         Translator translator = null;
         switch (req.transType()) {
         case Direct:
         translator = new DirectTranslator();
         break;
         case TPTPASO:
         translator = new TPTPASOTranslator();
         break;
         }

         if (kb.isEmpty()) kb = null;
         if (query.isEmpty()) query = null;
         return translator.translate(kb, query);
         //			List<String> l = list();
         //			if (kb != null && !kb.isEmpty())
         //				l = deserialize(translator.translateKB(kb));
         //
         //			if (query != null && !query.isEmpty())
         //				l.add(translator.translateQuery(query));
         } catch (TranslatorException e) {
         e.printStackTrace();
         return null;
         }
         *
         */
//		for (String str : l) {
//			System.out.println(str);
//		}
//		TptpDocument doc = new TptpDocument();
//		doc.setSentences(l);
//		return doc;
        //returing the kb
        /*
        try {
            File f = File.createTempFile("temp", ".txt");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(kb.getBytes());

            /*
            org.ruleml.psoa.absyntax.DefaultAbstractSyntax absSynFactory
                    = new DefaultAbstractSyntax();

            //org.ruleml.psoa.parser
            org.ruleml.psoa.parser.Parser p = new Parser();
            org.ruleml.psoa.absyntax.DefaultAbstractSyntax.Document doc = (org.ruleml.psoa.absyntax.DefaultAbstractSyntax.Document) p.parse(f, absSynFactory);

            System.out.println(doc.toString("  "));

            return doc.toString("  ");

            return "my string";
        } catch (Exception e) {
            e.printStackTrace();
            kb = e.getMessage();
        }
*/
        //return kb;
        if (clsOpDpInDomain.isEmpty())
            return "no hierarchy was returned: Error";
        else
            return clsOpDpInDomain;
    }

    /**
     * Print the class hierarchy for the given ontology from this class down,
     * assuming this class is at the given level. Makes no attempt to deal
     * sensibly with multiple inheritance.
     */
    private String printHierarchy(OWLClass clazz, OWLReasonerFactory reasonerFactory, OWLOntology ontology, PrintStream out) throws OWLException {
        String result = null;
        OWLReasoner reasoner = reasonerFactory
                .createNonBufferingReasoner(ontology);
        printHierarchy(reasoner, clazz, 0, ontology, System.out);
        /* Now print out any unsatisfiable classes */
        for (OWLClass cl : ontology.getClassesInSignature()) {
            if (!reasoner.isSatisfiable(cl)) {
                out.println("XXX: " + labelFor(cl, ontology));
                result += "\n" + "XXX: " + labelFor(cl, ontology);
            }
        }
        reasoner.dispose();
        return result;
    }

    private String labelFor(OWLClass clazz, OWLOntology ontology) {
        /*
         * Use a visitor to extract label annotations
         */
        LabelExtractor le = new LabelExtractor();
        Set<OWLAnnotation> annotations = clazz.getAnnotations(ontology);
        for (OWLAnnotation anno : annotations) {
            anno.accept(le);
        }
        /* Print out the label if there is one. If not, just use the class URI */
        if (le.getResult() != null) {
            return le.getResult().toString();
        } else {
            return clazz.getIRI().toString();
        }
    }

    /**
     * Print the class hierarchy from this class down, assuming this class is at
     * the given level. Makes no attempt to deal sensibly with multiple
     * inheritance.
     */
    private String
    printHierarchy(OWLReasoner reasoner, OWLClass clazz, int level, OWLOntology ontology, PrintStream out)
            throws OWLException {
        String result = null;
        /*
         * Only print satisfiable classes -- otherwise we end up with bottom
         * everywhere,
         */
        if (reasoner.isSatisfiable(clazz)) {
            for (int i = 0; i < level * INDENT; i++) {
                out.print(" ");
                result += " ";
            }
            out.println(labelFor(clazz, ontology));
            result += "\n" + labelFor(clazz, ontology);
            /* Find the children and recurse */
            for (OWLClass child : reasoner.getSubClasses(clazz, true)
                    .getFlattened()) {
                if (!child.equals(clazz)) {
                    printHierarchy(reasoner, child, level + 1, ontology, out);
                }
            }
        }
        return result;
    }
    @SuppressWarnings("deprecation")
    public static String decode(String s) {
        return URLDecoder.decode(s.replace("&gt;", ">"));
    }
}

