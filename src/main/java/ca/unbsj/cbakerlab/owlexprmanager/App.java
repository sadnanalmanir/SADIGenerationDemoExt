package ca.unbsj.cbakerlab.owlexprmanager;

import static ca.unbsj.cbakerlab.owlexprmanager.ClassExpressionTreeGenerator.setOfGraphs;

//import com.wcohen.ss.*;
//import com.wcohen.ss.lookup.*;
//import com.wcohen.ss.tokens.*;
//import com.wcohen.ss.api.*;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;
import com.tinkerpop.gremlin.java.GremlinPipeline;
//import com.wcohen.ss.JaroWinkler;
//import com.wcohen.ss.Level2Levenstein;
//import com.wcohen.ss.Levenstein;
//import com.wcohen.ss.SmithWaterman;
//import com.wcohen.ss.api.StringDistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxClassExpressionParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

//import com.wcohen.ss.*;
/**
 * Hello world!
 */

/**
 * Hello world!
 * 
 */
public class App {

	static final OWLOntologyManager manager = OWLManager
			.createOWLOntologyManager();
	static final OWLDataFactory df = OWLManager.getOWLDataFactory();
	static OWLOntology ontology = null;
	static IRI ontology_IRI = null;
	static IRI documentIRI = null;
	static ManchesterOWLSyntaxOWLObjectRendererImpl r = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	static ManchesterOWLSyntaxClassExpressionParser parser;
	// static ClassExpressionToTreeGenerator classExpressionToTreeGenerator =
	// new ClassExpressionToTreeGenerator();
	static OWLClassExpression eca;
	static OWLClass clsSADIInput = null;
	static OWLClass clsSADIOutput = null;
	static Set<OWLClassExpression> eqInpClasses = null;
	static Set<OWLClassExpression> eqOutputClasses = null;
	static ClassExpressionTreeGenerator classExpressionTreeGenerator;
	static DisjunctiveExpressionHandler disjunctiveExpressionHandler;
	// static SADIServiceCodeGenerator sadiServiceCodeGenerator;
	// static ServiceCodeGenerator serviceCodeGenerator;

	// Last update July 15, 2015
	static CodeGenerator codeGenerator;
	// For TPTP query generation
	static TPTPQueryGenerator tptpQueryGenerator;

	public static Set<Vertex> processedVertices;

	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/bmi-demo/bmi-demo-sadi-service-ontology.owl";
	// tested
	// static String classDescription =
	// "(has_height some (Measurement and (has_units value m) and (has_value some string)))";
	// static String classDescription =
	// "(has_height some (Measurement and (has_units value m) and (has_value some string))) and (has_mass some (Measurement and (has_units value kg) and (has_value some string)))";

	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/kegg/kegg-sadi-service-ontology.owl";
	// tested
	// static String classDescription =
	// "(((type value KEGG_Organism_Identifier) and (SIO_000300 some string)) and (hasContext some (type value Context)))";
	// getATCClassByKEGGDRUGID_Input
	// static String classDescription =
	// "(type value KEGG_DRUG_Identifier) and (SIO_000300 some string)";
	// getKEGGDRUGIDByATCClass_Input
	// static String classDescription = "subClassOf value SIO_010038";
	// not tested
	// getKEGGOrganismCodeByOrganismName_Input
	// static String classDescription =
	// "((((type value KEGG_Organism_Identifier) and (SIO_000300 some string)) and (hasContext some (type value Context))) or ((type value KEGG_Organism_Identifier) and (SIO_000300 some string)))";
	// static String classDescription =
	// "((((type value SIO_000116) and (SIO_000300 some string)) and (hasContext some (type value Context))) or ((type value SIO_000116) and (SIO_000300 some string)))";
	// getNameByKEGGDISEASEClass_Input
	// static String classDescription =
	// "(((hasContext some (type value Context)) and (SIO_000629 some ((SIO_000008 some ((type value KEGG_DISEASE_Identifier) and (SIO_000300 some string))) and (type value KEGG_DISEASE_Record)))) or (SIO_000629 some ((SIO_000008 some ((type value KEGG_DISEASE_Identifier) and (SIO_000300 some string))) and (type value KEGG_DISEASE_Record))))";

	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/util-sadi-services/util-sadi-services-ontology.owl";
	// tested
	// getWikipediaPageByTopic_Input
	// static String classDescription = "SIO_000300 some string";
	// getUniProtIDByUniProtEntryName_Input
	// static String classDescription =
	// "SIO_000116 and (SIO_000300 some string)";
	// static String classDescription =
	// "PMID_Identifier and (SIO_000300 some string)";
	// getGIByUniProtID_Input
	// static String classDescription =
	// "SIO_010015 and (SIO_000011 some (SIO_010043 and (SIO_000212 some UniProt_Record)))";
	// not tested
	// static String classDescription = "";
	// static String classDescription = "";
	// output classes
	// getHTMLByPMCID_Output
	// static String classDescription = SIO_000011 some (Document and (link some
	// string) and (format value \"text/html\"))
	// not tested
	// static String classDescription =
	// "SIO_000133 and (SIO_000011 some (((Document or SIO_000148)) and (link some string) and (format value \"text/html\")))";
	// static String classDescription = "((GO_0003674 or GO_0005575))";
	// static String classDescription =
	// "(link some string) or (SIO_000300 some string)";
	// static String classDescription =
	// "(link some string) or (SIO_000300 some string) or (SIO_000212 some UniProt_Record)";
	// static String classDescription =
	// "SIO_000011 some (subClassOf some ((GO_0003674)))";
	// static String classDescription =
	// "SIO_000011 some (subClassOf some ((GO_0003674 or GO_0005575)))";
	// static String classDescription =
	// "SIO_000011 some (subClassOf some ((GO_0003674 or GO_0005575 or GO_0008150)))";
	// static String classDescription =
	// "(SIO_000011 some (subClassOf some ((GO_0003674 or GO_0005575 or GO_0008150)))) and SIO_000011 some (subClassOf some ((GO_0003674 or GO_0005575)))";
	// working perferctly
	// static String classDescription =
	// "GO_0003674 and (SIO_000011 some (subClassOf some ((GO_0003674 or GO_0005575 or (SIO_000011 some (subClassOf some ((GO_0003674 or GO_0005575))))))))";

	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/icd/icd-sadi-service-ontology.owl";
	// tested
	// getSubclassByICD9DiseaseClass_Input
	// static String classDescription = "subClassOf value SIO_010299";
	// static String classDescription =
	// "(( (hasContext some Context) and (subClassOf value SIO_010299) ) )";
	// static String classDescription =
	// "((hasContext some Context) and (subClassOf value SIO_010299)) or (subClassOf value SIO_010299)";
	// static String classDescription = "";
	// static String classDescription = "";

	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/ddi/ddi-sadi-service-ontology.owl";
	// tested
	// getDrugDrugInteractionsByDrug_Input
	// static String classDescription =
	// "SIO_010038 and (SIO_000008 some (DRUG_BANK_Identifier and (SIO_000300 some string)))";

	// not in this ontology
	// static String classDescription =
	// "GO_0003674 and (SIO_000011 some (subClassOf some (SIO_000011 some (subClassOf some (GO_0005575)))))";
	// static String classDescription = "";

	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/temporal-reasoning/temporal-reasoning-sadi-service-ontology.owl";
	// tested
	// getTimeIntervalsByFinishingTimeInterval_Input
	// static String classDescription =
	// "FullyDefinedTimeInterval and (hasContext some Context)";
	// not working
	// static String classDescription = "FullyDefinedTimeInterval";
	// static String classDescription =
	// "FullyDefinedTimeInterval and (hasContext some Context)";
	// not tested
	// static String classDescription = "";
	// static String classDescription = "";

	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/uniprot/uniprot-sadi-service-ontology.owl";
	// tested
	//
	// static String classDescription = "SIO_000300 some string";
	// getUniProtIDByPMID_Input
	// static String classDescription =
	// "(type value PMID_Identifier) and (SIO_000300 some string)";
	// getUniProtIDByEC_Input
	// static String classDescription =
	// "(SIO_000629 some ((SIO_000008 some ((type value EC_Hierarchy_Identifier) and (SIO_000300 some string))) and (type value EC_Hierarchy_Record))) and (subClassOf value SIO_010343)";
	// a variation of the above service getUniProtIDByEC_Input
	// static String classDescription =
	// "(SIO_000629 some ((SIO_000008 some ((type value EC_Hierarchy_Identifier))) and (type value EC_Hierarchy_Record))) and (subClassOf value SIO_010343)";

	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/name-search/name-search-sadi-service-ontology.owl";
	// getSimilarICD10DiseaseNames_Input
	// static String classDescription =
	// "(((hasContext some (type value Context)) and (SIO_000300 some string)) or (SIO_000300 some string))";
	// testing the comparator based on length, we want the larger expressions to
	// be processed before we deal with the smaller expressions
	// static String classDescription =
	// "(SIO_000300 some string) or ((hasContext some (type value Context)) and (SIO_000300 some string))";

	/**
	 * Test cases for successful code generation from input class
	 */

	// OWLDataSomeValuesFrom
	// getWikipediaPageByTopic_Input
	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/util-sadi-services/util-sadi-services-ontology.owl";
	// static String classDescription = "SIO_000300 some string";

	// OWLObjectSomeValuesFrom
	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/icd/icd-sadi-service-ontology.owl";
	// static String classDescription = "hasContext some Context";

	// OWLObjectHasValue
	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/kegg/kegg-sadi-service-ontology.owl";
	// static String classDescription = "type value KEGG_Organism_Identifier";

	// OWLClass and OWLDataSomeValuesFrom
	// getUniProtIDByUniProtEntryName_Input
	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/util-sadi-services/util-sadi-services-ontology.owl";
	// static String classDescription =
	// "SIO_000116 and (SIO_000300 some string)";

	// OWLClass and OWLObjectSomeValuesFrom
	// getTimeIntervalsByFinishingTimeInterval_Input
	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/temporal-reasoning/temporal-reasoning-sadi-service-ontology.owl";
	// static String classDescription =
	// "FullyDefinedTimeInterval and (hasContext some Context)";

	// OWLObjectHasValue and OWLDataSomeValuesFrom
	// getATCClassByKEGGDRUGID_Input
	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/kegg/kegg-sadi-service-ontology.owl";
	// static String classDescription =
	// "(type value KEGG_DRUG_Identifier) and (SIO_000300 some string)";

	// OWLClass, OWLObjectSomeValuesFrom, OWLObjectHasValue,
	// OWLDataSomeValuesFrom
	// computeBMI_Input
	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/bmi-demo/bmi-demo-sadi-service-ontology.owl";
	// static String classDescription =
	// "(has_height some (Measurement and (has_units value m) and (has_value some string))) and (has_mass some (Measurement and (has_units value kg) and (has_value some string)))";
	// static String classDescription =
	// "(has_height some (Measurement and (has_units value m) and (has_value some string) )) and (has_mass some (Measurement and (has_value some string)))";
	// static String classDescription =
	// "(has_height some (Measurement and (has_units value m) and (has_value some string) )) and (has_mass some (Measurement))";
	// complex disjunction
	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/kegg/kegg-sadi-service-ontology.owl";
	// getKEGGOrganismCodeByOrganismName_Input
	// static String classDescription =
	// "((((type value KEGG_Organism_Identifier) and (SIO_000300 some string)) and (hasContext some (type value Context))) or ((type value KEGG_Organism_Identifier) and (SIO_000300 some string)))";
	// static String classDescription =
	// "(((type value KEGG_Organism_Identifier) and (SIO_000300 some string)) and (hasContext some (type value Context)))";
	// getNameByKEGGDISEASEClass_Input
	// static String classDescription =
	// "(((hasContext some (type value Context)) and (SIO_000629 some ((SIO_000008 some ((type value KEGG_DISEASE_Identifier) and (SIO_000300 some string))) and (type value KEGG_DISEASE_Record)))) or (SIO_000629 some ((SIO_000008 some ((type value KEGG_DISEASE_Identifier) and (SIO_000300 some string))) and (type value KEGG_DISEASE_Record))))";

	// tested
	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/kegg/kegg-sadi-service-ontology.owl";
	// static String classDescription =
	// "(((type value KEGG_Organism_Identifier) and (SIO_000300 some string)) and (hasContext some (type value Context)))";

	// OWLObjectHasValue and OWLDataSomeValuesFrom
	// getATCClassByKEGGDRUGID_Input
	// static String onlineOntology =
	// "http://cbakerlab.unbsj.ca:8080/kegg/kegg-sadi-service-ontology.owl";
	// static String classDescription =
	// "(type value KEGG_DRUG_Identifier) and (SIO_000300 some string)";
	// getKEGGDRUGIDByATCClass_Input
	// static String classDescription = "subClassOf value SIO_010038";

	static String onlineOntology = "http://cbakerlab.unbsj.ca:8080/lubm/lubm-sadi-service-ontology.owl";

	// static String classDescription = "Course and (name some string)";
	// static String classDescription =
	// "courseTaken some (Student and (name some string))";

	public App() {
		classExpressionTreeGenerator = new ClassExpressionTreeGenerator();
		disjunctiveExpressionHandler = new DisjunctiveExpressionHandler();
		// Obsolete
		// sadiServiceCodeGenerator = new SADIServiceCodeGenerator();
		// serviceCodeGenerator = new ServiceCodeGenerator();

		// updated
		// codeGenerator is the updated class for generating input/output code
		codeGenerator = new CodeGenerator();
		// class for generating the tptp query for feeding the Vampire Prime
		tptpQueryGenerator = new TPTPQueryGenerator();
		// obsolete
		// processedVertices = new LinkedHashSet<Vertex>();

	}

	public static void main(String[] args) throws OWLOntologyCreationException,
			ParserException {

		App app = new App();

		// if from a URI
		documentIRI = IRI.create(onlineOntology);
		// if from online
		ontology = manager.loadOntology(documentIRI);
		// OWLOntology ontology =
		// manager.loadOntologyFromOntologyDocument(documentIRI);

		String ipclassDescription = "Course and (name some string)";
		String opclassDescription = "courseTaken some (Student and (name some string))";

		// generates input tree/graph from the input class descriptions

		// Set<Graph> setOfGeneratedInputTrees =
		// handleClassExpressions(ipclassDescription);
		List<Graph> returnedInputGraph = getListOfGraphs();
		// assign degrees and auxiliary property to vertices for further
		// processing
		classExpressionTreeGenerator.setDegreeOfVertices(returnedInputGraph);
		classExpressionTreeGenerator
				.assignNodeVariableNames(returnedInputGraph);
		classExpressionTreeGenerator.displayTreeVertices(returnedInputGraph);
		classExpressionTreeGenerator.displayTreeEdges(returnedInputGraph);

		// generates output tree/graph from the output class descriptions
		// Set<Graph> setOfGeneratedOutputTrees =
		// handleClassExpressions(opclassDescription);
		List<Graph> returnedOutputGraph = classExpressionTreeGenerator
				.getMapOfGraphsAndCorrespondingEdges();
		// assign degrees and auxiliary property to vertices for further
		// processing
		classExpressionTreeGenerator.setDegreeOfVertices(returnedOutputGraph);
		classExpressionTreeGenerator
				.assignNodeVariableNames(returnedOutputGraph);
		classExpressionTreeGenerator.displayTreeVertices(returnedOutputGraph);
		classExpressionTreeGenerator.displayTreeEdges(returnedOutputGraph);

		/**
		 * To generate TPTP query we need a property e.g., tptpNodeName. We
		 * assign the root node first Then we assign the rest of the nodes by
		 * propagating parent nodes to child nodes if the edge is empty.
		 */
		classExpressionTreeGenerator.assignNodesForTPTP(returnedInputGraph);
		classExpressionTreeGenerator.assignNodesForTPTP(returnedOutputGraph);

		// One of the graph now has its root node assigned, the other doesn't
		// Assign the content for the other root node, not they both have common
		// Resource node
		String content = classExpressionTreeGenerator
				.getRootNodeContentForTPTPAssigned(returnedInputGraph);
		if (!content.equals(""))
			classExpressionTreeGenerator.assignContentoGraph(
					returnedOutputGraph, content);
		else {
			content = classExpressionTreeGenerator
					.getRootNodeContentForTPTPAssigned(returnedOutputGraph);
			if (!content.equals(""))
				classExpressionTreeGenerator.assignContentoGraph(
						returnedInputGraph, content);

		}

		// Propagate parent nodes to child nodes as done in variables
		classExpressionTreeGenerator
				.assignTPTPNodeVariableNames(returnedInputGraph);
		classExpressionTreeGenerator
				.assignTPTPNodeVariableNames(returnedOutputGraph);

		String tptpQuery = tptpQueryGenerator.generateTPTPQuery(ontology,
				returnedInputGraph, returnedOutputGraph);
		System.out.println(tptpQuery);

		/* generate and print input code */

		String inputCodeBlock = generateInputCode(ontology, returnedInputGraph);
		System.out.println(inputCodeBlock);

		// String outputCodeBlock = generateOutputCode(ontology,
		// returnedOutputGraph);
		// System.out.println(outputCodeBlock);
		// classExpressionTreeGenerator.displayTreeEdges(returnedOutputGraph);
		// classExpressionTreeGenerator.displayTreeVertices(returnedOutputGraph);
		// if trees need to be displayed here then use this
		/*
		 * for (Graph graph : setOfGraphs) { System.out.println();
		 * //System.out.println("Edges for Tree No: " + (i + 1));
		 * System.out.println(); System.out.println("Edges:  " + graph);
		 * System.out.println(); for (Edge edge : graph.getEdges()) {
		 * System.out.println(edge.getVertex(Direction.OUT).getId() + " # " +
		 * edge.getVertex(Direction.OUT).getProperty("name") + " -- ** " +
		 * edge.getLabel() + " ** --> " + edge.getVertex(Direction.IN).getId() +
		 * " # " + edge.getVertex(Direction.IN).getProperty("name")); } }
		 */

	}

	public static void processDegreeOfVertices(List<Graph> returnedGraph) {
		classExpressionTreeGenerator.setDegreeOfVertices(returnedGraph);
	}

	public static void processNodeVariableNames(List<Graph> returnedGraph) {
		classExpressionTreeGenerator.assignNodeVariableNames(returnedGraph);
	}

	public static String createTPTPQuery(OWLOntology ontology,
			List<Graph> returnedInputGraph, List<Graph> returnedOutputGraph) {
		return tptpQueryGenerator.generateTPTPQuery(ontology,
				returnedInputGraph, returnedOutputGraph);
	}

	public static List<Graph> getListOfGraphs() {
		return classExpressionTreeGenerator
				.getMapOfGraphsAndCorrespondingEdges();
	}

	public static String generateOutputCode(OWLOntology ontology,
			List<Graph> setOfGeneratedTrees, List<String> rdbTableColNames) {

		String outputCodeBlock = "";
		// Generate the code for the output RDF graph based on the output class
		// expression
		int graphCounter = 0;

		for (Graph g : setOfGeneratedTrees) {
			graphCounter++;
			List<Edge> edgesListSorted = sortEdgesInDFS(g.getEdges());

			// reorganize the graph
			// take help from
			// classExpressionTreeGenerator.assignNodeVariableNames(returnedInputGraph);

			// set the root variable name as output
			for (Vertex v : g.getVertices()) {
				if (v.getId().equals("0"))
					v.setProperty("nodeVariableName", "output");
			}
			// for every 'type' edge other than the root i.e. 'output' node, set
			// the 'nodeVariableName' as the Resource
			// name, which can be found by the 'name' property
			/*
			 * this seems to be an unnecessary solution
			 */
			/*
			 * for(Edge edge : edgesListSorted){
			 * if(edge.getLabel().equals("type") &&
			 * !edge.getVertex(Direction.OUT).getId().equals("0"))
			 * edge.getVertex(Direction.OUT).setProperty("nodeVariableName",
			 * edge.getVertex(Direction.IN).getProperty("name")); }
			 */
			// For each property edge that does not have a label, propagate the
			// nodeVariableName
			// of the parent to the child
			for (Edge edge : edgesListSorted) {
				if (edge.getLabel().equals(""))
					edge.getVertex(Direction.IN).setProperty(
							"nodeVariableName",
							edge.getVertex(Direction.OUT).getProperty(
									"nodeVariableName"));
			}

			/*
			 * Make a map of class/dataprop -> table column name. They can be
			 * looked up when creating the resource/attaching the literal from
			 * the SQL resultset e.g. rs.getString("table column name")
			 */
			/*
			 * Set<OWLClass> classes; Set<OWLDataProperty> dataProperties;
			 * Set<OWLObjectProperty> objectProperties;
			 * 
			 * classes = ontology.getClassesInSignature(); dataProperties =
			 * ontology.getDataPropertiesInSignature(); objectProperties =
			 * ontology.getObjectPropertiesInSignature(); int clspropCounter =
			 * 0;
			 */

			/**
			 * The order of the SELECT variables is probably based on the order
			 * of the attributes described in predicate_views.xml If they
			 * mapping does not work, check if the output graph ordering
			 * corresponds to the order of SELECT variables
			 */
			/*
			 * TODO add all the attributes from the predicate_views.xml to a
			 * listA get
			 */

			/*
			 * Map<String, String> clsprop2tableCol = new HashMap<String,
			 * String>(); for(Edge edge : edgesListSorted){ if (
			 * !edge.getLabel().equals("")){ //object property if (
			 * !checkPropertyType(objectProperties, dataProperties,
			 * edge.getLabel()) ){ if(edge.getLabel().equals("type")){
			 * clsprop2tableCol
			 * .put(edge.getVertex(Direction.IN).getProperty("name").toString(),
			 * rdbTableColNames.get(clspropCounter)); clspropCounter++; } else
			 * if (edge.getLabel().equals("subClassOf")){
			 * System.out.println("subClassOf is not implemented yet"); } }
			 * //data property else{ clsprop2tableCol.put(edge.getLabel(),
			 * rdbTableColNames.get(clspropCounter)); clspropCounter++; } } }
			 */

			if(rdbTableColNames.isEmpty())
				System.out.println("rdbTableColNames is empty");
			
			
			Map<String, Integer> colName2ColNum = new LinkedHashMap<String, Integer>();
			int colNum = 0;
			for (String colName : rdbTableColNames) {
				System.out.println("colun name->"+colName);
				colNum++;
				colName2ColNum.put(colName, colNum);
				System.out.println("colun no->"+colNum);
			}
			
			
			// List of classes and predicates related to the answer(A1, A2, ...,
			// An) in output tptp query
			List<String> outClsPredList = ca.unbsj.cbakerlab.owlexprmanager.TPTPQueryGenerator.answerClsPred;
			for (String item : outClsPredList) {
				System.out.println("--" + item);
			}

			/**
			 * Sort both (table columns) and (class and predicates) length-wise
			 */
			// THIS IS NOT NECESSARY ANYMORE, BOTH TABLE COLUMN NAMES AND CLASS/PREDICATE NAMES ARE ALREADY ORDERED
			/*
			Comparator<String> lengthwise = new Comparator<String>() {
				public int compare(String o1, String o2) {
					if (o1.length() > o2.length())
						return -1;

					if (o2.length() > o1.length())
						return 1;

					return 0;
				}
			};

			Collections.sort(outClsPredList, lengthwise);
			Collections.sort(rdbTableColNames, lengthwise);
			*/
			/* sorting ends*/
			
			Map<String, String> tableCol2clsProp = new HashMap<String,String>();
		    
		    
		    // do NOT use Symmetric-Core library, use StringDistance instead
		    //Using the StringDistance library
			// if this doesn't work, use new SmithWaterman();
		    
			//StringDistance distance = new ScaledLevenstein();
		    //SmithWaterman algorithm works as well
		    //StringDistance distance = new SmithWaterman();
		    
		    /*
		    //float result = 0;
		    int index = -1;
		    double score = 0.0;
		    for(int i = 0; i < rdbTableColNames.size()-1; i++){
		    //for(String t : rdbTableColNames){     
		        String t = rdbTableColNames.get(i);
		        System.out.println("#"+ t);
		        for(int j = 0; j < outClsPredList.size()-1; j++){
		        //for(String cp : outClsPredList){   
		            String cp = outClsPredList.get(j);
		            
		            System.out.println("  "+cp+"-> "+ distance.score(t, cp));
		            // StringDistance library
		            
		            if(score < distance.score(t, cp)){
		                score = distance.score(t, cp);
		                index++;
		            }
		            
		        }
		        tableCol2clsProp.put(t, outClsPredList.get(index));
		            outClsPredList.remove(index);
		        score = 0.0;
		        index = -1;
		        
		    }
		    
		    tableCol2clsProp.put(rdbTableColNames.get(rdbTableColNames.size()-1), outClsPredList.get(0));
		    */
			
			for(int i = 0, j = 0; i < rdbTableColNames.size() && j < outClsPredList.size(); i++, j++){				
				tableCol2clsProp.put(rdbTableColNames.get(i), outClsPredList.get(j));
			}
		    
		    
		    
		    
		    System.out.println("------------------------------------------------------------------");
		    if(tableCol2clsProp.isEmpty()){
		    	System.out.println("Error: No class or property found for the table column");
		    }else{
		    	for (String key: tableCol2clsProp.keySet()) {		    		
		    	    System.out.println("Table Column : " + key + "   -- matched to -->"+ " Class/Property : " + tableCol2clsProp.get(key));		    	    
		    	}
		    }
		    System.out.println("------------------------------------------------------------------");
		    

			// outputCodeBlock += codeGenerator.generateOPServiceCode(ontology,
			// df, parser, edgesListSorted, clsprop2tableCol);
		    
		    //old original
			//outputCodeBlock += codeGenerator.generateOPServiceCode(ontology,
				//	df, parser, edgesListSorted, colName2ColNum, tableCol2clsProp);
		    
		    
			outputCodeBlock += codeGenerator.modifiedgenerateOPServiceCode(ontology,
					df, parser, edgesListSorted, colName2ColNum, tableCol2clsProp);
		}// end for (Map.Entry

		return outputCodeBlock;
	}

	public static String generateInputCode(OWLOntology ontology,
			List<Graph> setOfGeneratedTrees) {

		String inputCodeBlock = "";
		// Generate the code for the input RDF graph based on the input class
		// expression
		int graphCounter = 0;

		for (Graph g : setOfGeneratedTrees) {
			graphCounter++;
			List<Edge> edgesList = sortEdgesInDFS(g.getEdges());

			inputCodeBlock += codeGenerator.generateIPServiceCode(ontology, df,
					parser, edgesList);

		}// end for (Map.Entry

		return inputCodeBlock;
	}

	public static Set<Graph> handleClassExpressions(String classDescription,
			ManchesterOWLSyntaxClassExpressionParser parser)
			throws OWLOntologyCreationException, ParserException {

		// parser = new
		// ManchesterOWLSyntaxClassExpressionParser(manager.getOWLDataFactory(),
		// new ShortFormEntityChecker(new
		// BidirectionalShortFormProviderAdapter(manager,
		// Collections.singleton(ontology), new SimpleShortFormProvider())));

		System.out.println(classDescription);
		/**
		 * check if the class expression expressed as String contains
		 * disjunctions
		 */
		boolean existDisjunctiveExpression = disjunctiveExpressionHandler
				.existDisjunctiveExpression(classDescription);
		// System.out.println(existDisjunctiveExpression);
		int classExpressionTreeInstance = 0;
		if (existDisjunctiveExpression == true) {
			System.out.println("The expression IS disjunctive");
			// Stack<String> setOfDisjunctiveExpressions =
			// disjunctiveExpressionHandler.handleDisjunctiveClassExpressions(parser,
			// classDescription);
			Stack<String> setOfDisjunctiveExpressions = sortSetOfDisjunctiveExpressions(disjunctiveExpressionHandler
					.handleDisjunctiveClassExpressions(parser, classDescription));

			// sort the stack of expressions so that larger expressions are
			// preferred for processing
			// setOfDisjunctiveExpressions =
			// sortSetOfDisjunctiveExpressions(setOfDisjunctiveExpressions);

			int sizeOfsetOfDisjunctiveExpressions = setOfDisjunctiveExpressions
					.size();
			// ClassExpressionTreeGenerator(sizeOfsetOfDisjunctiveExpressions);
			classExpressionTreeGenerator = new ClassExpressionTreeGenerator(
					sizeOfsetOfDisjunctiveExpressions);
			while (!setOfDisjunctiveExpressions.isEmpty()) {
				// this is a dynamic tree instance number to be generated before
				// popping expressions, e.g., 0, 1, 3, and so on
				classExpressionTreeInstance = sizeOfsetOfDisjunctiveExpressions
						- setOfDisjunctiveExpressions.size();
				System.out.println("################### "
						+ classExpressionTreeInstance);

				classDescription = setOfDisjunctiveExpressions.pop();
				System.out
						.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
				classExpressionTreeGenerator.generateTreeFromClassExpression(
						parser, classDescription, classExpressionTreeInstance);
			}
		} else {
			classExpressionTreeGenerator = new ClassExpressionTreeGenerator(1);
			System.out.println("The expression is NOT disjunctive");
			classExpressionTreeGenerator.generateTreeFromClassExpression(
					parser, classDescription, classExpressionTreeInstance);
		}

		Set<Graph> setOfGeneratedTrees = classExpressionTreeGenerator
				.getAllTrees();

		return setOfGeneratedTrees;
	}

	/**
	 * 
	 * @param edges
	 * @return sort the edges of the graph in ordre of generation i.e. DFS
	 */
	private static List<Edge> sortEdgesInDFS(Iterable<Edge> edges) {
		List<Edge> sortedEdges = new ArrayList<Edge>();

		for (Edge e : edges) {
			sortedEdges.add(e);
		}

		Collections.sort(sortedEdges, new Comparator<Edge>() {
			public int compare(Edge e1, Edge e2) {
				// based on number of edge ID
				return Integer.parseInt(e1.getId().toString())
						- Integer.parseInt(e2.getId().toString());
			}
		});

		return sortedEdges;
	}

	private static Stack<String> sortSetOfDisjunctiveExpressions(
			Stack<String> setOfDisjunctiveExpressions) {

		List<String> tempExpr = new ArrayList<String>();

		while (!setOfDisjunctiveExpressions.isEmpty()) {
			tempExpr.add(setOfDisjunctiveExpressions.pop());
		}

		Collections.sort(tempExpr, new Comparator<String>() {
			public int compare(String s1, String s2) {
				// based on string length
				// return s1.length() - s2.length();
				// based on number of tokens in the string
				return s1.split(" ").length - s2.split(" ").length;
			}
		});

		for (int i = 0; i < tempExpr.size(); i++) {
			setOfDisjunctiveExpressions.push(tempExpr.get(i));
		}
		return setOfDisjunctiveExpressions;
	}

	public static void processForTPTPAllNodes(List<Graph> returnedInputGraph,
			List<Graph> returnedOutputGraph) {
		System.out
				.println("-------------------------- new tptp node assigning -----------------------------------------");
		/**
		 * To generate TPTP query we need a property e.g., tptpNodeName. We
		 * assign the root node first Then we assign the rest of the nodes by
		 * propagating parent nodes to child nodes if the edge is empty.
		 */
		classExpressionTreeGenerator.assignNodesForTPTP(returnedInputGraph);
		classExpressionTreeGenerator.assignNodesForTPTP(returnedOutputGraph);

		// One of the graph now has its root node assigned, the other doesn't
		// Assign the content for the other root node, not they both have common
		// Resource node

		String content = classExpressionTreeGenerator
				.getRootNodeContentForTPTPAssigned(returnedInputGraph);
		if (!content.equals(""))
			classExpressionTreeGenerator.assignContentoGraph(
					returnedOutputGraph, content);
		else {
			content = classExpressionTreeGenerator
					.getRootNodeContentForTPTPAssigned(returnedOutputGraph);
			if (!content.equals(""))
				classExpressionTreeGenerator.assignContentoGraph(
						returnedInputGraph, content);

		}

		// Propagate parent nodes to child nodes as done in variables
		classExpressionTreeGenerator
				.assignTPTPNodeVariableNames(returnedInputGraph);
		classExpressionTreeGenerator
				.assignTPTPNodeVariableNames(returnedOutputGraph);

	}

	/**
	 * @param objectProperties
	 *            set of object properties
	 * @param dataProperties
	 *            set of data properties
	 * @param propertyLabel
	 *            edge label i.e. property name
	 * @return ture if the edge/property is data property
	 */
	private static boolean checkPropertyType(
			Set<OWLObjectProperty> objectProperties,
			Set<OWLDataProperty> dataProperties, String propertyLabel) {
		boolean result = false;

		for (OWLDataProperty dp : dataProperties) {
			if (dp.getIRI().getFragment().equals(propertyLabel)) {
				result = true;
			}
		}
		for (OWLObjectProperty op : objectProperties) {
			if (op.getIRI().getFragment().equals(propertyLabel)) {
				result = false;
			}
		}
		return result;
	}

}
