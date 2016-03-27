package ca.unbsj.cbakerlab.owlexprmanager;

import com.hp.hpl.jena.rdf.model.Resource;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxClassExpressionParser;
import org.semanticweb.owlapi.model.*;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * Generates the code for processing the input RDG graph according to the SADI
 * input class description Created by sadnana on 07/07/15.
 */
public class CodeGenerator {

	public String codeString = "";
	public Set<OWLClass> classes;
	public Set<OWLDataProperty> dataProperties;
	public Set<OWLObjectProperty> objectProperties;
	public Set<String> resourceCreated;
	 
	
	public String generateIPServiceCode(OWLOntology ontology,
			OWLDataFactory df, ManchesterOWLSyntaxClassExpressionParser parser,
			List<Edge> edgesList) {

		classes = ontology.getClassesInSignature();
		dataProperties = ontology.getDataPropertiesInSignature();
		objectProperties = ontology.getObjectPropertiesInSignature();

		for (Edge edge : edgesList) {
			// create code for those edge labels with object/data properties,
			// empty labels are not considered
			if (!edge.getLabel().equals("")) {
				// if the edge is an object property
				if (!checkPropertyType(objectProperties, dataProperties,
						edge.getLabel())) {
					// if the instance is corresponds to the root node
					if (edge.getLabel().equals("type")
							&& edge.getVertex(Direction.OUT).getId()
									.equals("0")) {
						if (isOWLClass(edge.getVertex(Direction.IN)
								.getProperty("name").toString(), classes)) {
							// System.out.println(edge.getVertex(Direction.IN).getProperty("name")
							// + " -- is an instance of OWLClass");
							codeString += createCodeForClassInstance(
									edge.getVertex(Direction.IN)
											.getProperty("nodeVariableName")
											.toString(),
									edge.getVertex(Direction.IN)
											.getProperty("name").toString(),
									edge.getVertex(Direction.OUT)
											.getProperty("nodeVariableName")
											.toString());
						} else {// probably not necessary anyway
							System.out.println("Not implemented yet");
							// codeString +=
							// createCodeForClassInstance(edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString(),
							// edge.getLabel(),
							// edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString());
						}
					} else if (edge.getLabel().equals("subClassOf")) {
						System.out
								.println("Code generation for subClassOf NOT implemented yet");
					}

					else {
						// for an object property, get the resource node
						// directly
						codeString += createCodeForResourceValue(
								edge.getVertex(Direction.IN)
										.getProperty("nodeVariableName")
										.toString(),
								edge.getLabel(),
								edge.getVertex(Direction.OUT)
										.getProperty("nodeVariableName")
										.toString());
					}
					/*
					 * //if(edge.getVertex(Direction.IN).getProperty("name")
					 * instanceof OWLClass) {
					 * if(isOWLClass(edge.getVertex(Direction
					 * .IN).getProperty("name").toString(), classes)){
					 * System.out
					 * .println(edge.getVertex(Direction.IN).getProperty("name")
					 * + " -- is an instance of OWLClass"); codeString +=
					 * createCodeForResourceValue
					 * (edge.getVertex(Direction.IN).getProperty
					 * ("nodeVariableName").toString(), edge.getLabel(),
					 * edge.getVertex
					 * (Direction.OUT).getProperty("nodeVariableName"
					 * ).toString()); } else { codeString +=
					 * createCodeForResourceStmt
					 * (edge.getVertex(Direction.IN).getProperty
					 * ("nodeVariableName").toString(), edge.getLabel(),
					 * edge.getVertex
					 * (Direction.OUT).getProperty("nodeVariableName"
					 * ).toString()); }
					 */

				} else {
					codeString += createCodeForDataValue(
							edge.getVertex(Direction.IN).getProperty("name")
									.toString(),
							edge.getVertex(Direction.IN)
									.getProperty("nodeVariableName").toString(),
							edge.getLabel(), edge.getVertex(Direction.OUT)
									.getProperty("nodeVariableName").toString());
				}

			}

		}

		return codeString;

	}

	private String createCodeForClassInstance(String objVarName,
			String className, String subVarName) {
		String instanceUriPref = "http://cbakerlab.unbsj.ca:8080/haiku/dw/model/function/";// Patient_by_patWID?wid=G1R-L1F-03";
		String result = "";

		result += "\n" + "\t\t" + "String " + subVarName.toUpperCase() + " = "
				+ subVarName + "." + "getURI()" + "." + "substring" + "(" + "("
				+ "\"" + instanceUriPref + className + "_" + "by" + "_" + "ID"
				+ "?" + "ID=" + "\"" + ")" + "." + "length()" + ")" + ";";

		result += "\n" + "\t\t" + "if" + "(" + subVarName.toUpperCase()
				+ " == " + "null" + ")" + "\n" + "\t\t" + "{" + "\n" + "\t\t"
				+ "\tlog.fatal" + "(" + "\"" + "Cannot extract "
				+ subVarName.toUpperCase() + " from the URI : " + "\"" + ")"
				+ ";" + "\n" + "\t\t" + "\tthrow new IllegalArgumentException"
				+ "(" + "\"" + "Cannot extract " + subVarName.toUpperCase()
				+ " from the URI : " + "\"" + ")" + ";" + "\n" + "\t\t" + "}";

		/*
		 * result += "\n" + "\t\t" + "Statement " + objVariableName + " = " +
		 * subVariableName + "." + "getProperty" + "(" + "Vocab" + "." +
		 * predicateName + ")";
		 * 
		 * result += "\n" + "\t\t" + "if" + "(" + objVariableName + " == " +
		 * "null" + ")" + "\n" + "\t\t" + "{" + "\n" + "\t\t" + "\tlog.fatal" +
		 * "(" + "\"" + "No " + objVariableName + " found in the input RDF for "
		 * + predicateName + "\"" + ")" + ";" + "\n" + "\t\t" +
		 * "\tthrow new IllegalArgumentException" + "(" + "\"" +
		 * "Cannot extract " + objVariableName +
		 * " from the input RDF attached to " + predicateName + "\"" + ")" + ";"
		 * + "\n" + "\t\t" + "}";
		 */
		return result + "\n";
	}

	private String createCodeForDataValue(String dataType,
			String objVariableName, String predicateName, String subVariableName) {
		String result = "";

		if (dataType.equals("string")) {
			result += "\n" + "\t\t" + "String " + "val" + objVariableName
					+ " = " + subVariableName + "." + "getProperty" + "("
					+ "Vocab" + "." + predicateName + ")" + "." + "getString"
					+ "(" + ")" + "." + "trim()" + ";";
			result += "\n" + "\t\t" + "if " + "(" + "!" + "val"
					+ objVariableName + ".startsWith(\"\\\"\")" + " && " + "!"
					+ "val" + objVariableName + ".endsWith(\"\\\"\")" + ")";
			result += "\n" + "\t\t\t" + "val" + objVariableName + " = "
					+ "\"\\\"\" + " + "val" + objVariableName + " + \"\\\"\";";
		} else if (dataType.equals("char")) {
			result += "\n" + "\t\t" + "Char " + "val" + objVariableName + " = "
					+ subVariableName + "." + "getProperty" + "(" + "Vocab"
					+ "." + predicateName + ")" + "." + "getChar" + "(" + ")"
					+ ";";
		} else if (dataType.equals("int")) {
			result += "\n" + "\t\t" + "int " + "val" + objVariableName + " = "
					+ subVariableName + "." + "getProperty" + "(" + "Vocab"
					+ "." + predicateName + ")" + "." + "getInt" + "(" + ")"
					+ ";";
		} else if (dataType.equals("float")) {
			result += "\n" + "\t\t" + "float " + "val" + objVariableName
					+ " = " + subVariableName + "." + "getProperty" + "("
					+ "Vocab" + "." + predicateName + ")" + "." + "getFloat"
					+ "(" + ")" + ";";
		} else if (dataType.equals("double")) {
			result += "\n" + "\t\t" + " " + "val" + objVariableName + " = "
					+ subVariableName + "." + "getProperty" + "(" + "Vocab"
					+ "." + predicateName + ")" + "." + "getDouble" + "(" + ")"
					+ ";";
		} else if (dataType.equals("boolean")) {
			result += "\n" + "\t\t" + "boolean " + "val" + objVariableName
					+ " = " + subVariableName + "." + "getProperty" + "("
					+ "Vocab" + "." + predicateName + ")" + "." + "getBoolean"
					+ "(" + ")" + ";";
		} else {
			System.out.println("Unknown data type in input RDF");
		}

		result += "\n" + "\t\t" + "if" + "(" + "val" + objVariableName + " == "
				+ "null" + ")" + "\n" + "\t\t" + "{" + "\n" + "\t\t"
				+ "\tlog.fatal" + "(" + "\"" + "No " + "val" + objVariableName
				+ " found in the input RDF for " + predicateName + "\"" + ")"
				+ ";" + "\n" + "\t\t" + "\tthrow new IllegalArgumentException"
				+ "(" + "\"" + "Cannot extract " + "val" + objVariableName
				+ " from the input RDF attached to " + predicateName + "\""
				+ ")" + ";" + "\n" + "\t\t" + "}";

		return result + "\n";
	}

	private boolean isOWLClass(String className, Set<OWLClass> classes) {
		for (OWLClass cls : classes) {
			if (className.equals(cls.getIRI().getFragment()))
				return true;
		}
		return false;
	}

	/*
	 * private String createCodeForResourceStmt(String objVariableName, String
	 * predicateName, String subVariableName) { String result = ""; result +=
	 * "\n" + "\t\t" + "Statement " + objVariableName + " = " + subVariableName
	 * + "." + "getProperty" + "(" + "Vocab" + "." + predicateName + ")";
	 * 
	 * result += "\n" + "\t\t" + "if" + "(" + objVariableName + " == " + "null"
	 * + ")" + "\n" + "\t\t" + "{" + "\n" + "\t\t" + "\tlog.fatal" + "(" + "\""
	 * + "No " + objVariableName + " found in the input RDF for " +
	 * predicateName + "\"" + ")" + ";" + "\n" + "\t\t" +
	 * "\tthrow new IllegalArgumentException" + "(" + "\"" + "Cannot extract " +
	 * objVariableName + " from the input RDF attached to " + predicateName +
	 * "\"" + ")" + ";" + "\n" + "\t\t" + "}";
	 * 
	 * return result + "\n"; }
	 */

	private String createCodeForResourceValue(String objVariableName,
			String predicateName, String subVariableName) {
		String result = "";
		if (!predicateName.equals("type")) {
			result += "\n" + "\t\t" + "Resource " + objVariableName + " = "
					+ subVariableName + "." + "getPropertyResourceValue" + "("
					+ "Vocab" + "." + predicateName + ");";

			result += "\n" + "\t\t" + "if" + "(" + objVariableName + " == "
					+ "null" + ")" + "\n" + "\t\t" + "{" + "\n" + "\t\t"
					+ "\tlog.fatal" + "(" + "\"" + "No " + objVariableName
					+ " found in the input RDF for " + predicateName + "\""
					+ ")" + ";" + "\n" + "\t\t"
					+ "\tthrow new IllegalArgumentException" + "(" + "\""
					+ "Cannot extract " + objVariableName
					+ " from the input RDF attached to " + predicateName + "\""
					+ ")" + ";" + "\n" + "\t\t" + "}";

		}
		return result + "\n";
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
	private boolean checkPropertyType(Set<OWLObjectProperty> objectProperties,
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

	public String generateOPServiceCode(OWLOntology ontology,
			OWLDataFactory df, ManchesterOWLSyntaxClassExpressionParser parser,
			List<Edge> edgesListSorted, Map<String, Integer> colName2ColNum,
			Map<String, String> tableCol2clsProp) {

		classes = ontology.getClassesInSignature();
		dataProperties = ontology.getDataPropertiesInSignature();
		objectProperties = ontology.getObjectPropertiesInSignature();

		for (String key : colName2ColNum.keySet()) {

			System.out.println("key : " + key);
			System.out.println("value : " + colName2ColNum.get(key));
		}

		String outputCodeString = "";

		outputCodeString += "\n\t\t" + "Model outputModel" + " = "
				+ "output.getModel();";
		outputCodeString += "\n\t\t"
				+ "Property type = outputModel.createProperty(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\");";
		outputCodeString += "\n\t\t" + "//Setting the type of common Resource";
		for (Edge edge : edgesListSorted) {
			if (edge.getVertex(Direction.OUT).getId().equals("0"))
				outputCodeString += "\n\t\t"
						+ edge.getVertex(Direction.OUT)
								.getProperty("nodeVariableName").toString()
						+ "."
						+ "addProperty"
						+ "("
						+ "type"
						+ ", "
						+ "outputModel"
						+ "."
						+ "createResource"
						+ "("
						+ "\""
						+ getClassUri(classes, edge.getVertex(Direction.OUT)
								.getProperty("tptpNodeName").toString()) + "\""
						+ ")" + ");";
		}
		// for every resource in the output graph, initialize a counter (we
		// don't know how many will be created, depends on size of ResultSet)
		// for (String key: clsprop2tableCol.keySet()) {
		// if(isClass(classes, key))
		// outputCodeString += "\n\t\t" + "int " + key + "Counter" + " = " +
		// "0;";
		// }

		outputCodeString += "\n\t\t" + "try" + " {";
		outputCodeString += "\n\t\t\t"
				+ "java.sql.Statement stmt = MySqlDatabase.connection.createStatement();";
		outputCodeString += "\n\t\t\t"
				+ "ResultSet rs = stmt.executeQuery(queryText);";
		outputCodeString += "\n\n\t\t\t" + "while(rs.next()) {";

		/*
		 * for (String key: colName2ColNum.keySet()) { if(isClass(classes, key))
		 * outputCodeString += "\n\t\t" + key + "Counter" + "++" + ";"; }
		 */

		String col = "";
		for (Edge edge : edgesListSorted) {
			if (!edge.getLabel().equals("")) {
				// resAttachedDataProp.add(
				// edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString());
				// if object property
				if (!checkPropertyType(objectProperties, dataProperties,
						edge.getLabel())) {
					if (edge.getLabel().equals("type")) {

					} else if (edge.getLabel().equals("subClassOf")) {
						System.out.println("subClassOf is not implemented yet");
					} else {
						// courseTaken(output, Student)
						// outputCodeString += createOpObjectPropStmt();
					}

					// if there is more than one output (multiple rows in SQL
					// resultset)
					// if(resAttachedDataProp.contains(edge.getVertex(Direction.IN).getProperty("nodeVariableName"))
					// ){
					// outputCodeString += "\n\t\t\t\t" + "tempResource" + " = "
					// + "outputModel" + "." + "createResource"
					// + "(\"" + getIRIForResource(classes, ontology,
					// edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString())+
					// "\"" + " + resourceCounter" + ");";
					// }

					// outputCodeString += "\n\t\t\t\t" +
					// edge.getVertex(Direction.OUT).getProperty("nodeVariableName")
					// +
					// "." + "addProperty" + "(" + "Vocab" + "." +
					// edge.getLabel() + ", " + "tempResource" + ");";
				}
				// data property
				else {
					// col = getCorrespondingTableCol(edge.getLabel(),
					// colName2ColNum);
					outputCodeString += createOpLiteralStmt(
							col,
							edge.getVertex(Direction.OUT)
									.getProperty("nodeVariableName").toString(),
							edge.getLabel(), edge.getVertex(Direction.IN)
									.getProperty("name").toString());
				}
			}
		}

		/*
		 * List<String> resAttachedDataProp = new ArrayList<String>();
		 * Map<String, Integer> colId = new HashMap<String, Integer>(); int
		 * colCounter = 0;
		 * 
		 * // if the output graph has a data property, store its subject
		 * Resource for(Edge edge : edgesListSorted){ if (
		 * checkPropertyType(objectProperties, dataProperties, edge.getLabel())
		 * ){ resAttachedDataProp.add(
		 * edge.getVertex(Direction.OUT).getProperty(
		 * "nodeVariableName").toString()); colCounter++;
		 * colId.put(edge.getLabel(), new Integer(colCounter)); } }
		 * 
		 * outputCodeString += "\n\t\t\t\t" + "resourceCounter" + "++" + ";";
		 * outputCodeString += "\n\t\t\t\t" + "Resource tempResource = null;";
		 * 
		 * for(Edge edge : edgesListSorted){ if ( !edge.getLabel().equals("") &&
		 * !edge.getLabel().equals("type") ){ //resAttachedDataProp.add(
		 * edge.getVertex
		 * (Direction.OUT).getProperty("nodeVariableName").toString()); // if
		 * object property if ( !checkPropertyType(objectProperties,
		 * dataProperties, edge.getLabel()) ){ // if there is more than one
		 * output (multiple rows in SQL resultset)
		 * if(resAttachedDataProp.contains
		 * (edge.getVertex(Direction.IN).getProperty("nodeVariableName")) ){
		 * outputCodeString += "\n\t\t\t\t" + "tempResource" + " = " +
		 * "outputModel" + "." + "createResource" + "(\"" +
		 * getIRIForResource(classes, ontology,
		 * edge.getVertex(Direction.IN).getProperty
		 * ("nodeVariableName").toString())+ "\"" + " + resourceCounter" + ");";
		 * }
		 * 
		 * outputCodeString += "\n\t\t\t\t" +
		 * edge.getVertex(Direction.OUT).getProperty("nodeVariableName") + "." +
		 * "addProperty" + "(" + "Vocab" + "." + edge.getLabel() + ", " +
		 * "tempResource" + ");"; } // data property else{ outputCodeString +=
		 * createLiteralStmt(colId, "tempResource", edge.getLabel(),
		 * edge.getVertex(Direction.IN).getProperty("name").toString()); } } }
		 */

		outputCodeString += "\n\t\t\t" + "} //end while";
		// outputCodeString += "\n\t\t\t" + "connection.close();" ;
		outputCodeString += "\n\t\t" + "} //end try";

		// outputCodeString += "\n\n\t\t" + "catch(ClassNotFoundException e){" ;
		// outputCodeString += "\n\t\t" + "e.printStackTrace();" ;
		// outputCodeString += "\n\t\t" + "}" ;

		outputCodeString += "\n\n\t\t" + "catch(Exception e){";
		outputCodeString += "\n\t\t\t"
				+ "log.fatal(\"Cannot execute query [\" + queryText + \"] or cannot read results:\" + e);";
		outputCodeString += "\n\t\t\t"
				+ "throw new RuntimeException(\"Cannot execute query [\" + queryText + \"] or cannot read results:\" + e,e);";
		outputCodeString += "\n\t\t" + "}";

		return outputCodeString;
	}

	private String getCorrespondingTableCol(String property,
			Map<String, String> clsprop2tableCol) {
		String result = "no column matched";
		for (String key : clsprop2tableCol.keySet()) {
			if (key.equals(property))
				result = clsprop2tableCol.get(key);
		}
		return result;
	}

	/**
	 * 
	 * 
	 * @param colId
	 * @param tempResource
	 *            subject of the statement
	 * @param predicate
	 *            predicate of the statement
	 * @param type
	 *            literal type of the statement
	 * @return
	 */
	private String createOpLiteralStmt(String colId, String subVar,
			String predicate, String type) {
		String litStmt = "";

		if (type.equals("string")) {
			litStmt += "\n\t\t\t\t" + subVar + "." + "addLiteral" + "("
					+ "Vocab" + "." + predicate + ", " + "rs" + "."
					+ "getString" + "(" + "\"" + colId + "\"" + ")" + ");";
		} else if (type.equals("int")) {
			litStmt += "\n\t\t\t\t" + subVar + "." + "addLiteral" + "("
					+ "Vocab" + "." + predicate + ", " + "rs" + "." + "getInt"
					+ "(" + "\"" + colId + "\"" + ")" + ");";
		} else if (type.equals("float")) {
			litStmt += "\n\t\t\t\t" + subVar + "." + "addLiteral" + "("
					+ "Vocab" + "." + predicate + ", " + "rs" + "."
					+ "getFloat" + "(" + "\"" + colId + "\"" + ")" + ");";
		} else
			System.out.println("Not implemented for this type" + type);

		return litStmt;
	}

	private int getColId(Map<String, Integer> colId, String predicate) {
		int colNum = 0;
		for (Map.Entry<String, Integer> cursor : colId.entrySet()) {
			if (cursor.getKey().equals(predicate))
				colNum = cursor.getValue();
		}
		return colNum;
	}

	/**
	 * 
	 * 
	 * @param classes
	 * @param ontology
	 * @param resourceName
	 * @return NS for the object or the object property
	 */
	private String getIRIForResource(Set<OWLClass> classes,
			OWLOntology ontology, String resourceName) {

		String resourceIRI = "";
		for (OWLClass res : classes) {
			if (res.getIRI().getFragment().equals(resourceName)) {
				resourceIRI = res.getIRI().toString();
			}
		}

		return resourceIRI;
	}

	private String getClassUri(Set<OWLClass> classes, String className) {
		for (OWLClass cls : classes) {
			if (cls.getIRI().getFragment().equals(className))
				return cls.getIRI().toString();
		}
		return "class IRI not found";
	}

	private boolean isClass(Set<OWLClass> classes, String className) {
		for (OWLClass cls : classes) {
			if (cls.getIRI().getFragment().equals(className))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param ontology
	 * @param property
	 * @return NS for the object or the data property
	 */
	private String getNSForProperty(OWLOntology ontology, String property) {
		Set<OWLDataProperty> dataProperties = ontology
				.getDataPropertiesInSignature();
		Set<OWLObjectProperty> objectProperties = ontology
				.getObjectPropertiesInSignature();
		String propertyNS = "";
		for (OWLDataProperty dp : dataProperties) {
			if (dp.getIRI().getFragment().equals(property)) {
				propertyNS = dp.getIRI().getNamespace();
			}
		}
		if (propertyNS.equals("")) {
			for (OWLObjectProperty op : objectProperties) {
				if (op.getIRI().getFragment().equals(property)) {
					propertyNS = op.getIRI().getNamespace();
				}
			}
		}
		return propertyNS;
	}

	public String modifiedgenerateOPServiceCode(OWLOntology ontology,
			OWLDataFactory df, ManchesterOWLSyntaxClassExpressionParser parser,
			List<Edge> edgesListSorted, Map<String, Integer> colName2ColNum,
			Map<String, String> tableCol2clsProp) {

		resourceCreated = new HashSet<String>();
		classes = ontology.getClassesInSignature();
		dataProperties = ontology.getDataPropertiesInSignature();
		objectProperties = ontology.getObjectPropertiesInSignature();
		
		String instanceUriPref = "http://cbakerlab.unbsj.ca:8080/haiku/dw/model/function/";		
		Integer currentDegree;
		String outputCodeString = "";
		
		outputCodeString += createCounterVariables(classes, edgesListSorted, tableCol2clsProp);
		

		outputCodeString += "\n\t\t" + "Model outputModel" + " = "
				+ "output.getModel();";
		outputCodeString += "\n\t\t"
				+ "Property type = outputModel.createProperty(\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\");";
		outputCodeString += "\n\t\t" + "//Setting the type of common Resource";
		
		for (Edge edge : edgesListSorted) {
			if (edge.getVertex(Direction.OUT).getId().equals("0")){
				outputCodeString += "\n\t\t"
						+ edge.getVertex(Direction.OUT)
								.getProperty("nodeVariableName").toString()
						+ "."+ "addProperty"+ "("+ "type"+ ", "+ "outputModel"+ "."+ "createResource"+ "("+ "\""
						+ getClassUri(classes, edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString()) + "\""+ ")" + ");";
			}
			resourceCreated.add(edge.getVertex(Direction.OUT)
					.getProperty("nodeVariableName").toString());
			break;// sets the root node type only once
		}// for loop sets the root node type once
		
		
		outputCodeString += "\n\t\t" + "try" + " {";
		outputCodeString += "\n\t\t\t"
				+ "java.sql.Statement stmt = MySqlDatabase.connection.createStatement();";
		outputCodeString += "\n\t\t\t"
				+ "ResultSet rs = stmt.executeQuery(queryText);";
		outputCodeString += "\n\n\t\t\t" + "while(rs.next()) {";
		
		
		for (Edge edge : edgesListSorted) {
			if (!edge.getLabel().equals("")) {

				// object property
				if (!checkPropertyType(objectProperties, dataProperties,
						edge.getLabel())) {
					/* outputclass subClassOf Patient */
					if (edge.getLabel().equals("type")) {
						currentDegree = edge.getVertex(Direction.OUT)
								.getProperty("degree");
						System.out.println("currentDegree = "+currentDegree);
						if (currentDegree == 1) {// only when 'outputclass subClassOf Patient' 
							if(isPartOfOutput(edge.getVertex(Direction.IN).getProperty("name").toString(), tableCol2clsProp)){
								
								
								outputCodeString += "\n\n\t\t\t\t" + "String " + edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
										+ " = " + "rs.getString("+ getColNum(edge.getVertex(Direction.IN).getProperty("name").toString(), tableCol2clsProp, colName2ColNum)+");";
								
								if(TPTPQueryGenerator.isDefinedEntity(edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString())
										&& isPartOfOutput(edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString(), tableCol2clsProp)){
									outputCodeString += "\n\t\t\t\t" 
										+ edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString() 
										+ " = "+ "outputModel.createResource(\""
										+ instanceUriPref + edge.getVertex(Direction.IN).getProperty("name").toString() + "_by_" + "ID" + "?" + "ID=" + "\" + "
										+  edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
										+ ");";
								}
								else{
									outputCodeString += "\n\t\t\t\t" 
											+ edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString() 
											+ " = "+ "outputModel.createResource(\""
											+ instanceUriPref + edge.getVertex(Direction.IN).getProperty("name").toString() + "_by_Custom_" + "ID" + "?" + "ID=" + "\" + "
											+  edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
											+ ");";
								}
								
								outputCodeString += "\n\t\t\t\t"
										+ edge.getVertex(Direction.OUT)
												.getProperty("nodeVariableName").toString()
										+ "." + "addProperty" + "(" + "type"
										+ ", " + "outputModel" + "." + "createResource" + "(" + "\"" + getClassUri(classes, edge.getVertex(Direction.OUT)
												.getProperty("tptpNodeName").toString()) + "\"" + ")" + ");";
							}
						}
						else if(currentDegree > 1) {// when 'outputclass subClassOf Patient and (has_facility_reference some (Facility and (has_facility_description some string)))' 
							if(isPartOfOutput(edge.getVertex(Direction.IN).getProperty("name").toString(), tableCol2clsProp)){
								
								
								outputCodeString += "\n\n\t\t\t\t" + "String " + edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
										+ " = " + "rs.getString("+ getColNum(edge.getVertex(Direction.IN).getProperty("name").toString(), tableCol2clsProp, colName2ColNum)+");";
								
								if(TPTPQueryGenerator.isDefinedEntity(edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString())
										&& isPartOfOutput(edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString(), tableCol2clsProp)){
									outputCodeString += "\n\t\t\t\t" 
										+ edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString() 
										+ " = "+ "outputModel.createResource(\""
										+ instanceUriPref + edge.getVertex(Direction.IN).getProperty("name").toString() + "_by_" + "ID" + "?" + "ID=" + "\" + "
										+  edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
										+ ");";
								}
								else{
									outputCodeString += "\n\t\t\t\t" 
											+ edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString() 
											+ " = "+ "outputModel.createResource(\""
											+ instanceUriPref + edge.getVertex(Direction.IN).getProperty("name").toString() + "_by_Custom_" + "ID" + "?" + "ID=" + "\" + "
											+  edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
											+ ");";
								}
								
								outputCodeString += "\n\t\t\t\t"
										+ edge.getVertex(Direction.OUT)
												.getProperty("nodeVariableName").toString()
										+ "." + "addProperty" + "(" + "type"
										+ ", " + "outputModel" + "." + "createResource" + "(" + "\"" + getClassUri(classes, edge.getVertex(Direction.OUT)
												.getProperty("tptpNodeName").toString()) + "\"" + ")" + ");";
							}
						}
					}
					// any object property other than type
					else{
						currentDegree = edge.getVertex(Direction.IN).getProperty("degree");						
                    	if(currentDegree > 0){ // just an object property
                    		// crating the parent resource node
                    		if(!resourceCreated.contains(edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString())){
                				// the parent resource creation is the same as data property parent resource creation
                				outputCodeString += createOutputResType(edge, instanceUriPref, tableCol2clsProp);
                				resourceCreated.add(edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString());
                			}
                    		// child node
                    		if(!resourceCreated.contains(edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString())){
                				// the parent resource creation is the same as data property parent resource creation
                				outputCodeString += createOutputObjChildResType(edge, instanceUriPref, tableCol2clsProp, colName2ColNum);
                				resourceCreated.add(edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString());
                			}
                    		outputCodeString += "\n\t\t\t\t"
                					+ edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString()
                					+ "."+ "addProperty" + "(" + "Vocab" + "." + edge.getLabel() + ", " 
                					+ edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString() + ");";
                    		
                    		
                    	}
                    	else{
                    		/*
                    		 *  output Resource node e.g.,  patient_has_diagnosis some Diagnosis, property value individual
                    		 *  although this block already means that the child node is part of output, just to make sure 
                    		 *  we create the parent node first 
                    		 */
                   		 	// crating the parent resource node
                    		if(!resourceCreated.contains(edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString())){
                				// the parent resource creation is the same as data property parent resource creation
                				outputCodeString += createOutputResType(edge, instanceUriPref, tableCol2clsProp);
                				resourceCreated.add(edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString());
                			}
                    		// child node should be a part of output, if not, something is wrong
                    		/*
                    		 *  this is where it is different from the data property where a resource is created rather than literal value,
                    		 *  but with the same principle applies. Kind of the resource creation for the 'type' object property 
                    		 */
                    		if(isPartOfOutput(edge.getVertex(Direction.IN).getProperty("name").toString(), tableCol2clsProp)){                    			
                    			String col = getColNum(edge.getVertex(Direction.IN).getProperty("name").toString(), tableCol2clsProp, colName2ColNum);                    		
                    			outputCodeString += createOutputObjResType(col, edge, instanceUriPref, tableCol2clsProp);
                    			
                    			outputCodeString += "\n\t\t\t\t"
                    					+ edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString()
                    					+ "."+ "addProperty" + "(" + "Vocab" + "." + edge.getLabel() + ", " 
                    					+ edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString() + ");";
                    		}
                    		else{
                    			/*  
                    			 * If not the output node, then just another child node 
                    			 */
                    			
                    			
                    		}
                    	}						
					}									
				}
				// data property
				else {
					String col = getColNum(edge.getLabel(), tableCol2clsProp, colName2ColNum);
					//create a resource and set its type only if it is not created
					if(!resourceCreated.contains(edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString())){
						outputCodeString += createOutputResType(edge, instanceUriPref, tableCol2clsProp);
						resourceCreated.add(edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString());
					}
					outputCodeString += createOutputLiteralStmt(col, edge,edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString(),edge.getLabel(), edge.getVertex(Direction.IN).getProperty("name").toString());
				}
				
			}
		}

		outputCodeString += "\n\n\t\t\t" + "} //end while";
		outputCodeString += "\n\n\t\t\t" + "if (rs != null) {";
		outputCodeString += "\n\t\t\t\t" + "rs.close();";
		outputCodeString += "\n\t\t\t" + "}";		
		outputCodeString += "\n\t\t\t" + "if (stmt != null) {";
		outputCodeString += "\n\t\t\t\t" + "stmt.close();";
		outputCodeString += "\n\t\t\t" + "}";
		
		
		outputCodeString += "\n\t\t" + "} //end try";

		// outputCodeString += "\n\n\t\t" + "catch(ClassNotFoundException e){" ;
		// outputCodeString += "\n\t\t" + "e.printStackTrace();" ;
		// outputCodeString += "\n\t\t" + "}" ;

		outputCodeString += "\n\n\t\t" + "catch(Exception e){";
		outputCodeString += "\n\t\t\t"
				+ "log.fatal(\"Cannot execute query [\" + queryText + \"] or cannot read results:\" + e);";
		outputCodeString += "\n\t\t\t"
				+ "throw new RuntimeException(\"Cannot execute query [\" + queryText + \"] or cannot read results:\" + e,e);";
		outputCodeString += "\n\t\t" + "}";

		return outputCodeString;
	}

	private String createOutputObjResType(String col, Edge edge, String instanceUriPref, Map<String, String> tableCol2clsProp) {
		String result = "";
		/**
		 * if the parent resource refers to a primary key in table e.g. p_Patient(entityForPatient(.....
		 * and if this resource is a part of output ( if not part of output, then no VALID patWID is known, so 
		 * the resource may reference to a patWID with completely wrong information, then use the custom ID as shown 
		 * in else block)
		 */
		if(TPTPQueryGenerator.isDefinedEntity(edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString())
				&& isPartOfOutput(edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString(), tableCol2clsProp)){
			result += "\n\t\t\t\t" 
				+ "Resource " + edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
				+ " = outputModel.createResource(\""
				+ instanceUriPref + edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString() + "_by_" + "ID" + "?" + "ID=" + "\" + "
				+  "rs.getString("+ col
				+ ");";
		}
		else{
			result += "\n\t\t\t\t" 
					+ "Resource " + edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
					+ " = outputModel.createResource(\""
					+ instanceUriPref + edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString() + "_by_Custom_" + "ID" + "?" + "ID=" + "\" + "
					+  "rs.getString("+ col
					+ ");";
		}
		result += "\n\t\t\t\t"
				+ edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
				+ "."+ "addProperty" + "(" + "type" + ", " + "outputModel" + "." + "createResource" + "(" + "\""
				+ getClassUri(classes, edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString()) + "\"" + ")" + ");";
		
		//result += "\n\t\t\t\t" + edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString()+"Counter" + "++;";
		
		return result;
	}

	private String createOutputResType(Edge edge, String instanceUriPref, Map<String, String> tableCol2clsProp) {
		String result = "";
		/**
		 * if the parent resource refers to a primary key in table e.g. p_Patient(entityForPatient(.....
		 * and if this resource is a part of output ( if not part of output, then no VALID patWID is known, so 
		 * the resource may reference to a patWID with completely wrong information, then use the custom ID as shown 
		 * in else block)
		 */
		if(TPTPQueryGenerator.isDefinedEntity(edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString())
				&& isPartOfOutput(edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString(), tableCol2clsProp)){
			result += "\n\t\t\t\t" 
				+ "Resource " + edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString()
				+ " = outputModel.createResource(\""
				+ instanceUriPref + edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString() + "_by_" + "ID" + "?" + "ID=" + "\" + "
				+  edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString()+"Counter"
				+ ");";
		}
		else{
			result += "\n\t\t\t\t" 
					+ "Resource " + edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString()
					+ " = outputModel.createResource(\""
					+ instanceUriPref + edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString() + "_by_Custom_" + "ID" + "?" + "ID=" + "\" + "
					+  edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString()+"Counter"
					+ ");";
		}
		result += "\n\t\t\t\t"
				+ edge.getVertex(Direction.OUT).getProperty("nodeVariableName").toString()
				+ "."+ "addProperty" + "(" + "type" + ", " + "outputModel" + "." + "createResource" + "(" + "\""
				+ getClassUri(classes, edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString()) + "\"" + ")" + ");";
		
		result += "\n\t\t\t\t" + edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString()+"Counter" + "++;";
		
		return result;
	}

	
	private String createOutputObjChildResType(Edge edge, String instanceUriPref, Map<String, String> tableCol2clsProp, Map<String, Integer> colName2ColNum) {
		String result = "";
		/**
		 * if the parent resource refers to a primary key in table e.g. p_Patient(entityForPatient(.....
		 * and if this resource is a part of output ( if not part of output, then no VALID patWID is known, so 
		 * the resource may reference to a patWID with completely wrong information, then use the custom ID as shown 
		 * in else block)
		 */
		if(TPTPQueryGenerator.isDefinedEntity(edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString())
				&& isPartOfOutput(edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString(), tableCol2clsProp)){
			result += "\n\t\t\t\t" 
				+ "Resource " + edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
				+ " = outputModel.createResource(\""
				+ instanceUriPref + edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString() + "_by_" + "ID" + "?" + "ID=" + "\" + "
				// change this line below to rs.getString(col)
				//+  edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString()+"Counter"
				+ "rs.getString("+ getColNum(edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString(), tableCol2clsProp, colName2ColNum)+")"
				+ ");";
		}
		else{
			result += "\n\t\t\t\t" 
					+ "Resource " + edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
					+ " = outputModel.createResource(\""
					+ instanceUriPref + edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString() + "_by_Custom_" + "ID" + "?" + "ID=" + "\" + "
					+  edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString()+"Counter"
					+ ");";
		}
		result += "\n\t\t\t\t"
				+ edge.getVertex(Direction.IN).getProperty("nodeVariableName").toString()
				+ "."+ "addProperty" + "(" + "type" + ", " + "outputModel" + "." + "createResource" + "(" + "\""
				+ getClassUri(classes, edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString()) + "\"" + ")" + ");";
		
		result += "\n\t\t\t\t" + edge.getVertex(Direction.IN).getProperty("tptpNodeName").toString()+"Counter" + "++;";
		
		return result;
	}
	
	
	
	private String createOutputLiteralStmt(String colId, Edge edge, String subVar,
			String predicate, String type) {
		
		String litStmt = "";
		
		
		if (type.equals("string")) {
			litStmt += "\n\t\t\t\t" + subVar + "." + "addLiteral" + "("
					+ "Vocab" + "." + predicate + ", " + "rs" + "."
					+ "getString" + "(" + colId + ")" + ");";
		} else if (type.equals("int")) {
			litStmt += "\n\t\t\t\t" + subVar + "." + "addLiteral" + "("
					+ "Vocab" + "." + predicate + ", " + "rs" + "." + "getInt"
					+ "("  + colId +  ")" + ");";
		} else if (type.equals("float")) {
			litStmt += "\n\t\t\t\t" + subVar + "." + "addLiteral" + "("
					+ "Vocab" + "." + predicate + ", " + "rs" + "."
					+ "getFloat" + "("  + colId  + ")" + ");";
		} else
			System.out.println("Not implemented for this type" + type);

		return litStmt;		
	}

	private String createCounterVariables(Set<OWLClass> classes, List<Edge> edgesListSorted,
			Map<String, String> tableCol2clsProp) {
		
		Set<String> counterVars = new HashSet<String>();
		String result = "";
		
		for (String clsOrProp : tableCol2clsProp.values()) {
			
			if(isClass(classes, clsOrProp)){
				counterVars.add(clsOrProp);
				//result += "\n\t\t" + "int " + clsOrProp + "Counter = 0;";
			}
			else{
				System.out.println(clsOrProp + "is a property");
				for(Edge edge : edgesListSorted){
					if(edge.getLabel().equals(clsOrProp)){
						counterVars.add(edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString());
						//result += "\n\t\t" + "int " + edge.getVertex(Direction.OUT).getProperty("tptpNodeName").toString() + "Counter = 0;";
					}
				}
			}
			
		}
		
		for(String countVar : counterVars){
			result += "\n\t\t" + "int " + countVar + "Counter = 0;";
		}
		
		return result;
	}

	private String getColNum(String clsOrProp,
			Map<String, String> tableCol2clsProp,
			Map<String, Integer> colName2ColNum) {
		String keyclsOrProp2NextMap = "";
		for (Entry<String, String> entry : tableCol2clsProp.entrySet()) {
            if (entry.getValue().equals(clsOrProp)) {
                System.out.println(entry.getKey());
                keyclsOrProp2NextMap = entry.getKey();
            }
        }
		
		int colNum = 0;
		for (Map.Entry<String, Integer> cursor : colName2ColNum.entrySet()) {
			if (cursor.getKey().equals(keyclsOrProp2NextMap))
				colNum = cursor.getValue();
		}
		return String.valueOf(colNum);		
	}

	private boolean isPartOfOutput(String clsOrProp,
			Map<String, String> tableCol2clsProp) {
		
		for (String clsProp : tableCol2clsProp.values()) {
			if (clsProp.equals(clsOrProp))
				return true;
		}		
		return false;
	}
}