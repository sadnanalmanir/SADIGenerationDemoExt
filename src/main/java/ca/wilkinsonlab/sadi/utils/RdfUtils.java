package ca.wilkinsonlab.sadi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import ca.wilkinsonlab.sadi.Config;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class RdfUtils
{
	private static final Logger log = Logger.getLogger(RdfUtils.class);
	
	/**
	 * Convert a collection of Jena Triples to a Jena Model.
	 * @param triples the collection of triples
	 * @return a new model containing the triples
	 */
	public static Model triplesToModel(Collection<Triple> triples)
	{
		Model model = ModelFactory.createMemModelMaker().createFreshModel();
		for (Triple triple: triples)
			addTripleToModel(model, triple);
		return model;
	}
	
	/**
	 * Convert a Jena Model to a collection of triples.
	 * @param model
	 * @return a collection of triples representing the model
	 */
	public static Collection<Triple> modelToTriples(Model model) {
		
		Collection<Triple> triples = new ArrayList<Triple>();
		StmtIterator i = model.listStatements();
		while(i.hasNext()) {
			Statement stmt = i.next();
			Triple triple = new Triple(
					stmt.getSubject().asNode(),
					stmt.getPredicate().asNode(),
					stmt.getObject().asNode());
			triples.add(triple);
		}
		return triples;
	}
	
	/**
	 * Add the specified Triple to the specified Model.
	 * @param model the model
	 * @param triple the triple
	 */
	public static void addTripleToModel(Model model, Triple triple)
	{
		Resource s = getResource(model, triple.getSubject());
		Property p = getProperty(model, triple.getPredicate());
		RDFNode o = getRDFNode(model, triple.getObject());
		model.add(s, p, o);
	}
	
	public static RDFNode getRDFNode(Model model, Node node)
	{
		if (node.isLiteral())
			return getLiteral(model, node);
		else
			return getResource(model, node);
	}
	
	public static Resource getResource(Model model, Node node)
	{
		if (node.isBlank())
			return model.createResource(node.getBlankNodeId());
		else if (node.isURI())
			return model.createResource(node.getURI());
		else
			throw new IllegalArgumentException(String.format("node %s is not a resource", node));
	}
	
	public static Literal getLiteral(Model model, Node node)
	{
		if (!node.isLiteral())
			throw new IllegalArgumentException(String.format("node %s is not a literal", node));
			
		return ResourceFactory.createTypedLiteral(node.getLiteralLexicalForm(), node.getLiteralDatatype());
	}
	
	public static Property getProperty(Model model, Node node)
	{
		if (node.isURI())
			return model.createProperty(node.getURI());
		else
			throw new IllegalArgumentException(String.format("node %s is not a resource", node));
	}
	
	public static String logStatements(Model model)
	{
		return logStatements("", model);
	}
	
	public static String logStatements(String prefix, Model model)
	{
		StringBuilder buf = new StringBuilder();
		
		StmtIterator iter = model.listStatements();
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();
		    Resource  subject   = stmt.getSubject();
		    Property  predicate = stmt.getPredicate();
		    RDFNode   object    = stmt.getObject();
		    
		    buf.append(prefix);
		    buf.append(subject.toString());
		    buf.append(" ");
		    buf.append(predicate.toString());
		    buf.append(" ");
		    if (object.isLiteral())
		    	buf.append("\"");
		    buf.append(object.toString());
		    if (object.isLiteral())
		    	buf.append("\"");
		    if (iter.hasNext())
		    	buf.append(" .\n");
		}
		
	    return buf.toString();
	}

	/**
	 * @author Ben Vandervalk
	 * @param objectValue
	 * @return
	 */
	public static boolean isURI(String objectValue)
	{
		if (StringUtils.isEmpty(objectValue))
			return false;
	
		try {
			// TODO is "UTF-8" really required here?
			URI uri = new URI(objectValue, false, "UTF-8");
			if (uri.isAbsoluteURI())
				return true;
			else
				return false;
		} catch (URIException e) {
			return false;
		}
	}
	
	/**
	 * Returns true if the specified resource has at least one rdf:type,
	 * false otherwise.
	 * @param resource the resource
	 * @return true if the specified resource has at least one rdf:type,
	 *         false otherwise.
	 */
	public static boolean isTyped(Resource resource)
	{
		return resource.hasProperty(RDF.type);
	}

	/** 
	 * Extract a collection of triples from an RDF input stream.  The encoding
	 * of the incoming RDF is specified by 'lang' (e.g. "RDF/XML").  The possible
	 * values for 'lang' are the same as for Jena's 'Model.read' method:
	 * "RDF/XML", "N-TRIPLES", and "N3".
	 * 
	 * The main caveat of this method is that it must load the entire set
	 * of triples into memory. 
	 * 
	 * @param input The RDF to be converted to triples.  
	 * @param lang The encoding of the RDF. 
	 * @return a collection of triples.
	 */
	public static Collection<Triple> getTriples(InputStream input, String lang)
	{
		// Use a Jena model to convert RDF/XML => triples.
		Model model = ModelFactory.createMemModelMaker().createFreshModel();
		model.read(input, "", lang);
		
		Collection<Triple> triples = new ArrayList<Triple>();
		for (StmtIterator i = model.listStatements(); i.hasNext(); )
			triples.add(i.nextStatement().asTriple());
		return triples;
	}

	/**
	 * Return the string representation of a Jena Node, without surrounding
	 * brackets or quotes, and without any xsd:datatype suffix.
	 * 
	 * The default string representation of a node in Jena quotes literals 
	 * (including numbers) and appends the associated xsd:datatype.  However,
	 * Jena does not include angle brackets around the absolute datatype URI,
	 * so the default string form will not parse within a standard SPARQL query.  
	 * Also, the Jena string representation of a variable doesn't include the 
	 * preceding "?".
	 * 
	 * @param node
	 * @return
	 */
	public static String getPlainString(Node node)
	{
		String str;
		if(node.isURI()) 
			str = node.toString();
		else if(node.isVariable()) 
			str = "?" + node.getName();
		else if(node.isBlank()) 
			str = node.getBlankNodeLabel().toString(); 
		else 
//			str = node.getLiteral().getLexicalForm().toString();
			str = node.getLiteralLexicalForm();
		return str;
	}
	
	public static String getPlainString(RDFNode node)
	{
		return getPlainString(node.asNode());
	}
	
	/**
	 * Attempt to parse the specified literal as a boolean.
	 * In addition to the actual typed literal true, the strings
	 * "true", "t", "on", "1", etc., will be parsed as true.
	 * @param literal
	 * @return
	 */
	public static Boolean getBoolean(Literal literal)
	{
		try {
			return literal.getBoolean();
		} catch (Exception e) { } 
		try {
			int i = literal.getInt();
			if (i == 1)
				return true;
			else if (i == 0)
				return false;
		} catch (Exception e) { }
		String s = literal.getLexicalForm();
		if (s.equalsIgnoreCase("true") ||
				s.equalsIgnoreCase("t") ||
				s.equalsIgnoreCase("yes") ||
				s.equalsIgnoreCase("y") ||
				s.equalsIgnoreCase("1")) {
			return true;
		} else if (s.equalsIgnoreCase("false") ||
				s.equalsIgnoreCase("f") ||
				s.equalsIgnoreCase("no") ||
				s.equalsIgnoreCase("n") ||
				s.equals("0")) {
			return false;
		}
		return null;
	}
	
	public static Literal createTypedLiteral(String jenaToString)
	{
		int splitPoint = jenaToString.lastIndexOf("^^");
		if (splitPoint < 0)
			return ResourceFactory.createPlainLiteral(jenaToString);
		
		String value = jenaToString.substring(0, splitPoint);
		String datatypeURI = jenaToString.substring(splitPoint+2);
		RDFDatatype datatype = TypeMapper.getInstance().getTypeByName(datatypeURI);
		return ResourceFactory.createTypedLiteral(value, datatype);
	}
	
	public static Collection<Resource> extractResources(Collection<RDFNode> nodes) 
	{
		Collection<Resource> resources = new ArrayList<Resource>(nodes.size());
		for (RDFNode node :nodes) {
			if (node.isResource()) {
				resources.add(node.as(Resource.class));
			}
		}
		return resources;
	}
	
	public static Collection<Literal> extractLiterals(Collection<RDFNode> nodes) 
	{	
		Collection<Literal> literals = new ArrayList<Literal>(nodes.size());
		for (RDFNode node :nodes) {
			if (node.isLiteral()) {
				literals.add(node.as(Literal.class));
			}
		}
		return literals;
	}
	
	/**
	 * Create a new memory model and read the contents of the argument,
	 * which can be either a local path or a remote URL.
	 * @param pathOrURL a local path or a remote URL
	 * @return the new model
	 * @throws IOException if the argument is an invalid URL and can't be read locally
	 */
	public static Model createModelFromPathOrURL(String pathOrURL) throws IOException
	{
		Model model = ModelFactory.createDefaultModel();
		return loadModelFromPathOrURL(model, pathOrURL);
	}
	
	/**
	 * Read the contents of a local path or a remote URL into the specified model.
	 * @param model the model 
	 * @param pathOrURL a local path or a remote URL
	 * @return the model
	 * @throws IOException if the argument is an invalid URL and can't be read locally
	 */
	public static Model loadModelFromPathOrURL(Model model, String pathOrURL) throws IOException
	{
		try {
			URL url = new URL(pathOrURL);
			log.debug(String.format("identified %s as a URL", pathOrURL));
			model.read(url.toString());
			return model;
		} catch (MalformedURLException e) {
			log.debug(String.format("%s is not a URL: %s", pathOrURL, e.getMessage()));
		}
		log.debug(String.format("identified %s as a path", pathOrURL));
		try {
			File f = new File(pathOrURL);
			model.read(new FileInputStream(f), "");
			return model;
		} catch (FileNotFoundException e) {
			log.error(String.format("error reading RDF from %s: %s", pathOrURL, e.toString()));
			throw new IOException(String.format("%s did not parse as a URL and could not be read as a file: %s", pathOrURL, e.getMessage()));
		}
	}
	
	/**
	 * Adds common namespace prefixes to the specified model.
	 * @param model the model
	 */
	public static void addNamespacePrefixes(Model model)
	{
		Configuration nsConfig = Config.getConfiguration().subset("sadi.ns");
		for (Iterator<?> keys = nsConfig.getKeys(); keys.hasNext();) {
			String key = (String)keys.next();
			model.setNsPrefix(key, nsConfig.getString(key));
		}
	}
	
//	/**
//	 * Write a collection of triples to a file, as RDF.  
//	 * @param filename The name of the file to write to
//	 * @param triples The triples to write
//	 * @param rdfFormat This value is passed onto Jena's model.write() method.  Possible
//	 * values include "RDF/XML" and "N3".
//	 * @throws IOException if there is a problem opening or writing to the file
//	 */
//	public static void writeTriplesAsRDF(String filename, Collection<Triple> triples, String rdfFormat) throws IOException 
//	{
//		FileOutputStream fos = new FileOutputStream(filename);
//		writeTriplesAsRDF(fos, triples, rdfFormat);
//		fos.close();
//	}
//	
//	/**
//	 * Write a collection of triples to an output stream, as RDF.  
//	 * @param os The output stream
//	 * @param triples The triples to write
//	 * @param rdfFormat This value is passed onto Jena's model.write() method.  Possible
//	 * values include "RDF/XML" and "N3".
//	 * @throws IOException if there is a problem writing to the stream
//	 */
//	public static void writeTriplesAsRDF(OutputStream os, Collection<Triple> triples, String rdfFormat) throws IOException
//	{
//		Model model = modelMaker.createFreshModel();
//		for (Triple triple : triples) {
//			Resource s = model.createResource(triple.getSubject().toString());
//			Property p = model.createProperty(triple.getPredicate().toString());
//			String obj = triple.getObject().toString();
//			RDFNode o;
//			if (TriplesHelper.isURI(obj))
//				o = new ResourceImpl(StringUtil.escapeURI(obj));
//			else
//				o = model.createLiteral(obj);
//			model.add(s,p,o);
//		}
//		try {
//			model.write(os, rdfFormat);
//		}
//		finally {
//			model.close();
//		}
//	}
}
