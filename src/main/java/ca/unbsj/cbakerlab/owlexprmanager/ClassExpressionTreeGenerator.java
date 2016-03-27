/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ca.unbsj.cbakerlab.owlexprmanager;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase.DIRECTION;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxClassExpressionParser;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

/**
 * This Class generates a Java tree based on the TinkerTop Blueprint library
 * from the disjunction-free OWLClassExpression
 *
 * @author sadnana
 */
public class ClassExpressionTreeGenerator {


    //public static int nodeCounter;
    public static int treeInstanceIndex;
    Graph[] graph;
    Stack<String> nodeList = new Stack<String>();
    Stack<Vertex> vertices = new Stack<Vertex>();
    static ManchesterOWLSyntaxOWLObjectRendererImpl r = new ManchesterOWLSyntaxOWLObjectRendererImpl();
    static String propSomeValuesFrom = "";
    static Set<Graph> setOfGraphs = new LinkedHashSet<Graph>();
    List<Graph> correspondingGraphEdgesMap;

    // holds all the edges of the graph as they were inserted
    List<Edge> edgesCreatedByInsertOrder = new ArrayList<Edge>();

    static int commonNodeCounter = 0;
    static int otherNodeCounter = 0;
    static int leafNodeCounter = 0;


    public ClassExpressionTreeGenerator() {
    }

    /**
     * Constructor to initialize the number of trees to be generated
     *
     * @param sizeOfsetOfDisjunctiveExpressions set the number of tree instances
     */
    public ClassExpressionTreeGenerator(int sizeOfsetOfDisjunctiveExpressions) {

        graph = new TinkerGraph[sizeOfsetOfDisjunctiveExpressions];

        // graph added in order of generation for LinkedHashMap
        correspondingGraphEdgesMap = new ArrayList<Graph>(sizeOfsetOfDisjunctiveExpressions);

    }

    /**
     * Creates a tree instance corresponding to the OWLClassExpression as String
     * Add all trees to a single set after the generation process
     *
     * @param parser to parse a OWLClassExpression string into a OWLClassExpression
     * @param desc disjunction-free OWLClassExpression
     * @param treeInstanceID tree instance number e.g., 0, 1, ... , n
     */
    void generateTreeFromClassExpression(ManchesterOWLSyntaxClassExpressionParser parser, String desc, int treeInstanceID) {

        treeInstanceIndex = treeInstanceID;
        OWLClassExpression owlClassExpr;
        try {
            owlClassExpr = parser.parse(desc);
            graph[treeInstanceIndex] = new TinkerGraph();
            generateTree(owlClassExpr);
            setOfGraphs.add(graph[treeInstanceIndex]);

            correspondingGraphEdgesMap.add(graph[treeInstanceIndex]);

        } catch (ParserException ex) {
            Logger.getLogger(ClassExpressionTreeGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Recursively traverses the OWLClassExpression, breaks it down in the process and checks the current OWLClassExpression is an instance
     * This method stores all the OWLClassExpression vertices either atomic or non-atomic in nodeList
     * Properties are edges and any entity is a node with the edge between entities 
     *
     * @param owlClassExpr OWLClassExpression to build tree from
     */
    private void generateTree(OWLClassExpression owlClassExpr) {

        if (owlClassExpr instanceof OWLObjectIntersectionOf) {
            Vertex v = graph[treeInstanceIndex].addVertex(null);
            v.setProperty("name", r.render(owlClassExpr));
            v.setProperty("degree", new Integer(0));
            nodeList.push(r.render(owlClassExpr));
            if (!propSomeValuesFrom.equals("")) {
                if (!vertices.isEmpty()) {
                    Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v, propSomeValuesFrom);
                    edgesCreatedByInsertOrder.add(e);
                }
                propSomeValuesFrom = "";
            } else if (!vertices.isEmpty()) {
                Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v, "");

                edgesCreatedByInsertOrder.add(e);
            }
            for (int i = 0; i < ((OWLObjectIntersectionOf) owlClassExpr).getOperands().size(); i++) {
                vertices.push(v);
            }
            for (OWLClassExpression y : ((OWLObjectIntersectionOf) owlClassExpr).getOperands()) {
                generateTree(y);
            }
        }

        if (owlClassExpr instanceof OWLClass) {
            Vertex v = graph[treeInstanceIndex].addVertex(null);
            v.setProperty("name", r.render(owlClassExpr));
            v.setProperty("degree", new Integer(0));
            nodeList.push(r.render(owlClassExpr));

            ((OWLClass) owlClassExpr).getIRI().getFragment();
            if (!vertices.isEmpty()) {
                Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v, "type");
                edgesCreatedByInsertOrder.add(e);
            }// if the input class refers to a class e.g., getServiceTest_Input = Work
             // i.e. there is only a single resource is defined by the input
            else if(edgesCreatedByInsertOrder.isEmpty()){
            	//create the new vertex
            	Vertex v1 = graph[treeInstanceIndex].addVertex(null);
                v1.setProperty("name", r.render(owlClassExpr));
                v1.setProperty("degree", new Integer(0));
                
                nodeList.push(r.render(owlClassExpr));
            	Edge e = graph[treeInstanceIndex].addEdge(null, v, v1, "type");
                edgesCreatedByInsertOrder.add(e);
            }
        }

        if (owlClassExpr instanceof OWLObjectSomeValuesFrom) {
            Vertex v1 = graph[treeInstanceIndex].addVertex(null);
            v1.setProperty("name", r.render(owlClassExpr));
            v1.setProperty("degree", new Integer(0));
            nodeList.push(r.render(owlClassExpr));
            if (!vertices.isEmpty()) {
                if (!propSomeValuesFrom.equals("")) {
                    Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v1, propSomeValuesFrom);
                    edgesCreatedByInsertOrder.add(e);
                    propSomeValuesFrom = "";
                } else {
                    Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v1, "");
                    edgesCreatedByInsertOrder.add(e);
                }
            }
            vertices.push(v1);
            String prop = ((OWLObjectSomeValuesFrom) owlClassExpr).getProperty().asOWLObjectProperty().getIRI().getFragment();
            propSomeValuesFrom = prop;

            if (((OWLObjectSomeValuesFrom) owlClassExpr).getFiller() instanceof OWLClass) {
                Vertex v2 = graph[treeInstanceIndex].addVertex(null);
                v2.setProperty("name", r.render(((OWLObjectSomeValuesFrom) owlClassExpr).getFiller()));
                v2.setProperty("degree", new Integer(0));
                nodeList.push(r.render(((OWLObjectSomeValuesFrom) owlClassExpr).getFiller()));
                if (!vertices.isEmpty()) {
                    Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v2, prop);
                    edgesCreatedByInsertOrder.add(e);
                }
                // once property is used, the static property is reset
                propSomeValuesFrom = "";
            } else {
                generateTree(((OWLObjectSomeValuesFrom) owlClassExpr).getFiller());
            }
        }

        if (owlClassExpr instanceof OWLObjectHasValue) {
            Vertex v1 = graph[treeInstanceIndex].addVertex(null);
            v1.setProperty("name", r.render(owlClassExpr));
            v1.setProperty("degree", new Integer(0));
            nodeList.push(r.render(owlClassExpr));

            if (!vertices.isEmpty()) {
                // works "if object property some (hasValue)"
                if (!propSomeValuesFrom.equals("")) {
                    Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v1, propSomeValuesFrom);
                    edgesCreatedByInsertOrder.add(e);
                    propSomeValuesFrom = "";
                } else {
                    Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v1, "");
                    edgesCreatedByInsertOrder.add(e);
                }
            }
            String prop = ((OWLObjectHasValue) owlClassExpr).getProperty().asOWLObjectProperty().getIRI().getFragment();
            ((OWLObjectHasValue) owlClassExpr).getValue().asOWLNamedIndividual().getIRI().getFragment();
            Vertex v2 = graph[treeInstanceIndex].addVertex(null);
            v2.setProperty("name", ((OWLObjectHasValue) owlClassExpr).getValue().asOWLNamedIndividual().getIRI().getFragment());
            v2.setProperty("degree", new Integer(0));
            nodeList.push(((OWLObjectHasValue) owlClassExpr).getValue().asOWLNamedIndividual().getIRI().getFragment());

            if (prop.equals("subClassOf")) {
                Edge e = graph[treeInstanceIndex].addEdge(null, v1, v2, prop);
                edgesCreatedByInsertOrder.add(e);
            } else if (prop.equals("type")) {
                Edge e = graph[treeInstanceIndex].addEdge(null, v1, v2, prop);
                edgesCreatedByInsertOrder.add(e);
            } else {
                Edge e = graph[treeInstanceIndex].addEdge(null, v1, v2, prop);
                edgesCreatedByInsertOrder.add(e);
            }
        }

        if (owlClassExpr instanceof OWLDataHasValue) {
            Vertex v1 = graph[treeInstanceIndex].addVertex(null);
            v1.setProperty("name", r.render(owlClassExpr));
            v1.setProperty("degree", new Integer(0));
            nodeList.push(r.render(owlClassExpr));

            if (!vertices.isEmpty()) {
                // works "if object property some (hasValue)"
                if (!propSomeValuesFrom.equals("")) {
                    Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v1, propSomeValuesFrom);
                    edgesCreatedByInsertOrder.add(e);
                    propSomeValuesFrom = "";
                } else {
                    Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v1, "");
                    edgesCreatedByInsertOrder.add(e);
                }
            }
            String prop = ((OWLDataHasValue) owlClassExpr).getProperty().asOWLDataProperty().getIRI().getFragment();
            ((OWLDataHasValue) owlClassExpr).getValue().getDatatype().getIRI().getFragment();
            Vertex v2 = graph[treeInstanceIndex].addVertex(null);
            v2.setProperty("name", ((OWLDataHasValue) owlClassExpr).getValue().getDatatype().getIRI().getFragment());
            v2.setProperty("degree", new Integer(0));
            nodeList.push(((OWLDataHasValue) owlClassExpr).getValue().getDatatype().getIRI().getFragment());

            if (prop.equals("subClassOf")) {
                Edge e = graph[treeInstanceIndex].addEdge(null, v1, v2, prop);
                edgesCreatedByInsertOrder.add(e);
            } else if (prop.equals("type")) {
                Edge e = graph[treeInstanceIndex].addEdge(null, v1, v2, prop);
                edgesCreatedByInsertOrder.add(e);
            } else {
                Edge e = graph[treeInstanceIndex].addEdge(null, v1, v2, prop);
                edgesCreatedByInsertOrder.add(e);
            }
        }

        if (owlClassExpr instanceof OWLDataSomeValuesFrom) {
            Vertex v1 = graph[treeInstanceIndex].addVertex(null);
            v1.setProperty("name", r.render(owlClassExpr));
            v1.setProperty("degree", new Integer(0));
            nodeList.push(r.render(owlClassExpr));
            if (!vertices.isEmpty()) {
                Edge e = graph[treeInstanceIndex].addEdge(null, vertices.pop(), v1, "");
                edgesCreatedByInsertOrder.add(e);
            }

            String prop = ((OWLDataSomeValuesFrom) owlClassExpr).getProperty().asOWLDataProperty().getIRI().getFragment();
            ((OWLDataSomeValuesFrom) owlClassExpr).getFiller().asOWLDatatype().toString();
            Vertex v2 = graph[treeInstanceIndex].addVertex(null);
            v2.setProperty("name", ((OWLDataSomeValuesFrom) owlClassExpr).getFiller().asOWLDatatype().getIRI().getFragment());
            v2.setProperty("degree", new Integer(0));
            nodeList.push(((OWLDataSomeValuesFrom) owlClassExpr).getFiller().asOWLDatatype().getIRI().getFragment());

            Edge e = graph[treeInstanceIndex].addEdge(null, v1, v2, prop);
            edgesCreatedByInsertOrder.add(e);
        }

    }

    /**
     * Return the vertices of all trees
     */
    public void displayTreeVertices(List<Graph> existingGraph) {

        /*
         System.out.println();
         for (int i = 0; i <= treeInstanceIndex; i++) {
         System.out.println();
         System.out.println("Vertices for Tree No: " + (i + 1));
         System.out.println();
         for (Vertex vertex : graph[i].getVertices()) {
         System.out.println(vertex + " --> " + vertex.getProperty("name"));
         }
         }
         */
        int graphCounter = 0;
        for( Graph g : existingGraph){
            graphCounter++;
            System.out.println();
            System.out.println("Vertices for Tree No: " + graphCounter);
            System.out.println();
            for (Vertex vertex : g.getVertices()) {

                System.out.println(vertex +  " #Children =  "+ vertex.getProperty("degree") +" --> " + vertex.getProperty("name"));
                System.out.println(" --> " + vertex.getProperty("nodeVariableName"));
            }
        }


    }

    /**
     * Return the edges of all trees
     */
    public void displayTreeEdges(List<Graph> existingGraph) {
        /*

         for (int i = 0; i <= treeInstanceIndex; i++) {
         System.out.println("Edges for Tree No: " + (i + 1));
         System.out.println();
         System.out.println("Edges of " + graph[i]);
         System.out.println();
         for (Edge edge : graph[i].getEdges()) {
         //System.out.println(edge);
         System.out.println(edge.getVertex(Direction.OUT).getId() + " # " + edge.getVertex(Direction.OUT).getProperty("name") + " -- ** " + edge.getLabel() + " ** --> " + edge.getVertex(Direction.IN).getId() + " # " + edge.getVertex(Direction.IN).getProperty("name"));
         }
         }

         */
        int graphCounter = 0;
        for (Graph g : existingGraph) {
            graphCounter++;
            System.out.println();
            System.out.println("Edges for Tree No: " + graphCounter);
            System.out.println();
            System.out.println("Edges  " + g);
            System.out.println();
            for (Edge edge : g.getEdges()) {
                System.out.println(edge.getVertex(Direction.OUT).getId() + " # " + edge.getVertex(Direction.OUT).getProperty("name") + " -- ** " + edge.getLabel() + " ** --> " + edge.getVertex(Direction.IN).getId() + " # " + edge.getVertex(Direction.IN).getProperty("name"));
                System.out.println("------------------------------------------");
            }
        }

    }

    /**
     * Return the set of all trees generated
     * @return set of trees
     */
    Set<Graph> getAllTrees() {
        return setOfGraphs;
    }

    public List<Graph> getMapOfGraphsAndCorrespondingEdges(){
        return correspondingGraphEdgesMap;
    }

    /**
     * Set a degree property to each vertices. 
     * Get the current degree of a vertex and increment if there is an outgoing edge from that vertex
     */












    public void setDegreeOfVertices(List<Graph> existingGraph) {
        Integer currentDegree;
        int updatedDegree;
        for (Graph g : existingGraph) {
            for (Edge edge : g.getEdges()) {
                // get the vertex from which this edge is originating
                Vertex v = edge.getVertex(Direction.OUT);
                // get the current degree as an Element object and cast it to int
                currentDegree = v.getProperty("degree");
                // increment the degree and set it
                updatedDegree = currentDegree + 1;
                v.setProperty("degree", new Integer(updatedDegree));
            }
        }
    }

    /**
     * Assign names of variables as vertex property 'nodeVariableName' for either Resource, Data value.
     *
     */
    public void assignNodeVariableNames(List<Graph> existingGraph) {
        int numberOfVertices = 0;
        Integer currentDegree;
        for (Graph g : existingGraph) {
            // get the number of vertices
            Iterable<Vertex> vit = g.getVertices();
            Iterator it = vit.iterator();
            while(it.hasNext()){
                numberOfVertices++;
                it.next();
            }

            //System.out.println(" NUMOFVERTICES "+numberOfVertices);
            // get the sorted edges traversed in DFS order
            List<Edge> edgesListSorted = sortEdgesInDFS(g.getEdges());
            List<Vertex> verticesListSorted = sortVerticesInDFS(edgesListSorted, numberOfVertices);
            System.out.println("Sorted Vertices: "+verticesListSorted.toString());
            System.out.println("Sorted Edges: "+edgesListSorted.toString());

            for(Vertex v : verticesListSorted){
                // set the root node as input
                if(v.getId().equals("0"))
                    v.setProperty("nodeVariableName", "input");
                else{
                    // assign empty content for each non-root node
                    v.setProperty("nodeVariableName", "");
                    // if the vertex is NOT the root (i.e. Resource input) and common, set and increment the name
                    // get the current degree as an Element object and cast it to int
                    currentDegree = v.getProperty("degree");
                    if( currentDegree > 1 )
                        v.setProperty("nodeVariableName", "common" + "ResStmt" + (++commonNodeCounter));

                    //
                    if( currentDegree == 1 ) {
                        Edge e = getIncomingEdge(v, edgesListSorted);
                        if ( !e.getLabel().equals(""))
                            v.setProperty("nodeVariableName", "ResStmt" + (++otherNodeCounter));
                    }
                    // Assign nodeVariableName property to the leaf node(s) with degree = 0
                    if( currentDegree == 0 ) {
                        // clean the node content of 'name' and, set and increment it
                        v.setProperty("nodeVariableName", getSimpleName(v.getProperty("name").toString()) + "Node" + (++leafNodeCounter));

                    }
                }

            }

            // For each property edge that does not have a label, propagate the nodeVariableName
            // of the parent to the child
            for(Edge edge : edgesListSorted){
                if(edge.getLabel().equals(""))
                    //System.out.println("edge "+edge.getId()+ " is empty");

                    edge.getVertex(Direction.IN).setProperty("nodeVariableName", edge.getVertex(Direction.OUT).getProperty("nodeVariableName"));
            }




        }
    }

    /**
     *
     * @param v vertex having the incoming edge
     * @param edgesListSorted list of sorted edges for this graph
     * @return incoming edge  for the vertex
     */
    private Edge getIncomingEdge(Vertex v, List<Edge> edgesListSorted) {
        Edge edge = null;
        for(Edge e : edgesListSorted){
            if(e.getVertex(Direction.IN).equals(v))
                return e;
        }
        return edge;
    }



    private List<Vertex> sortVerticesInDFS(List<Edge> edgesListSorted, int numOfVertices) {
        List<Vertex> sortedVertexList = new ArrayList<Vertex>(numOfVertices);

        for(Edge edge : edgesListSorted){
            if(!sortedVertexList.contains(edge.getVertex(Direction.OUT)))
                sortedVertexList.add(edge.getVertex(Direction.OUT));
            if(!sortedVertexList.contains(edge.getVertex(Direction.IN)))
                sortedVertexList.add(edge.getVertex(Direction.IN));

        }
        return sortedVertexList;
    }

    private List<Edge> sortEdgesInDFS(Iterable<Edge> edges) {
        List<Edge> sortedEdges = new ArrayList<Edge>();

        for(Edge e : edges){
            sortedEdges.add(e);
        }

        Collections.sort(sortedEdges, new Comparator<Edge>() {
            public int compare(Edge e1, Edge e2) {
                //based on number of edge ID
                return Integer.parseInt(e1.getId().toString()) - Integer.parseInt(e2.getId().toString());
            }
        });


        return sortedEdges;
    }

    private String getSimpleName(String name)
    {
        String newName = Pattern.compile("[^\\w-]").matcher(name).replaceAll(" ");
        if (!newName.equals(name)) {
            newName = WordUtils.capitalizeFully(newName);
            newName = Pattern.compile("\\s+").matcher(newName).replaceAll("");
        }
        return newName;
    }

    /**
     * We don't know whether input or output class description had the root node defined. 
     * We find which graph has the root node and then assign the node
     * @param returnedInputGraph
     */
	public void assignNodesForTPTP(List<Graph> existingGraph) {
		//Map<List<Graph>, String> graphRootTPTPContent = new HashMap<List<Graph>, String>();
		for (Graph g : existingGraph) {
            List<Edge> edgesListSorted = sortEdgesInDFS(g.getEdges());
            
            for(Vertex v : g.getVertices()){
            	v.setProperty("tptpNodeName", "");
            }
            // The root node will indicate the class that is attached to the graph with vertex id '0'
            for(Edge e : edgesListSorted){
            	//if(e.getLabel().equals("type") && e.getVertex(Direction.OUT).getId().equals("0")){ // this only works for the root node
            	if(e.getLabel().equals("type")){ // works for all Resources            		
            		e.getVertex(Direction.OUT).setProperty("tptpNodeName", e.getVertex(Direction.IN).getProperty("name"));
            		
            	}
            	else if(e.getLabel().equals("subClassOf") && e.getVertex(Direction.OUT).getId().equals("0")){
            		//e.getVertex(Direction.OUT).setProperty("tptpNodeName", e.getVertex(Direction.IN).getProperty("name"));
            		System.out.println("Has not been implemented yet: Modify assignRootForTPTP method");
            	}
            }
            
            
            //for(Vertex v : g.getVertices()){
            	//System.out.println(v.getId().toString() + "-> " + v.getPropertyKeys().toString());
            //}
            
		}
	}

	


	public String getRootNodeContentForTPTPAssigned(List<Graph> existingGraph) {
		String nodeContent = "";
		for (Graph g : existingGraph) {
            for(Vertex v : g.getVertices()){
            	if(v.getId().equals("0"))
            		nodeContent = v.getProperty("tptpNodeName").toString();
            }            
		}
		return nodeContent;
	}
	public void assignContentoGraph(List<Graph> existingGraph,
			String content) {
		for (Graph g : existingGraph) {
            for(Vertex v : g.getVertices()){
            	if(v.getId().equals("0"))
            		v.setProperty("tptpNodeName", content);
            }            
		}
		
		
	}

	public void assignTPTPNodeVariableNames(List<Graph> existingGraph) {
		
        for (Graph g : existingGraph) {
            List<Edge> edgesListSorted = sortEdgesInDFS(g.getEdges());
            for(Edge edge : edgesListSorted){
                if(edge.getLabel().equals(""))
                    edge.getVertex(Direction.IN).setProperty("tptpNodeName", edge.getVertex(Direction.OUT).getProperty("tptpNodeName"));
            }
        }
	}
	
}