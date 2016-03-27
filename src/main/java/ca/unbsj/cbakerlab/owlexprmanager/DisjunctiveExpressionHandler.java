/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.unbsj.cbakerlab.owlexprmanager;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 *
 * @author sadnana
 */
public class DisjunctiveExpressionHandler {

    static ManchesterOWLSyntaxOWLObjectRendererImpl r = new ManchesterOWLSyntaxOWLObjectRendererImpl();

    // holds disjunctive OWLClassExpressions
    static Set<OWLClassExpression> orClasses;
    // holds current class expression under processing which has disjunctive OWLClassExpressions, part of the whole expression
    static String currentUnionOfExpressions;
    // holds expression having disjunctive OWLClassExpression 
    static Stack<String> expressionsToProcess;
    // holds expression without disjunctive OWLClassExpression after processing
    static Stack<String> processedExpressions;

    /**
     * initialize data structures
     */
    public DisjunctiveExpressionHandler() {
        orClasses = new HashSet<OWLClassExpression>();
        expressionsToProcess = new Stack<String>();
        processedExpressions = new Stack<String>();
        currentUnionOfExpressions = "";
    }

    /**
     * Returns true/false depending on the existence of disjunctive
     * OWLClassExpressions by finding out the first occurrence of the string
     * 'or'
     *
     *
     * @param classDescription the OWLClassExpression currently under
     * consideration in String form
     * @return answer if the OWLClassExpression has disjunction
     */
    boolean existDisjunctiveExpression(String classDescription) {
        if (classDescription.indexOf(" or ") != -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the OWLClassExpressions free of any disjunctive
     * OWLClassExpressions
     *
     * @param parser to parse a OWLClassExpression string into a
     * OWLClassExpression
     * @param classDescription the OWLClassExpression currently under
     * consideration in String form
     * @return stack containing disjunction-free OWLClassExpression
     */
    Stack<String> handleDisjunctiveClassExpressions(ManchesterOWLSyntaxClassExpressionParser parser, String classDescription) {
        // store the original class expression to process onto stack
        expressionsToProcess.push(classDescription);
        /**
         * Start with the given expression and process each expression until
         * there are any more expressions in stack
         */
        while (!expressionsToProcess.isEmpty()) {
            try {
                classDescription = r.render(parser.parse(expressionsToProcess.pop())).replaceAll("\\s+", " ");
                OWLClassExpression classDescriptionInOWL = parser.parse(classDescription);

                splitIntoDisjunctiveExpressions(parser, classDescription, classDescriptionInOWL);

            } catch (ParserException ex) {
                Logger.getLogger(DisjunctiveExpressionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return processedExpressions;
    }

    /**
     * Splits the original OWLClassExpression into disjunctive OWLClassExpressions
     * Extracts the OWLClassExpression operands and stores them as String 
     * It is ensured that the String form is free from unnecessary whitespace characters
     * 
     * @param parser to parse a OWLClassExpression string into a
     * OWLClassExpression
     * @param classDescription the OWLClassExpression currently under
     * consideration in String form
     * @param classDescriptionInOWL the OWLClassExpression currently under
     * consideration
     */
    private void splitIntoDisjunctiveExpressions(ManchesterOWLSyntaxClassExpressionParser parser, String classDescription, OWLClassExpression classDescriptionInOWL) {

        Set<String> disjunctSetOfClasses = new HashSet<String>();

        generateDisjunctExpr(classDescriptionInOWL);

        for (OWLClassExpression oce : orClasses) {
            disjunctSetOfClasses.add(r.render(oce).replaceAll("\\s+", " "));
        }

        for (String disjuncSetElement : disjunctSetOfClasses) {
            System.out.println("set --> " + disjuncSetElement);
            createExpressionWithoutDisjunction(classDescription, currentUnionOfExpressions, disjuncSetElement);
        }

    }

    /**
     * Finds the OWLClassExpression consisting of all disjunctive expressions
     * from the original OWLClassExpression and replaces that expression with a
     * single OWLClassExpression.
     * <p>
     * It checks if the new expression is disjunction-free, if not then stores
     * it to process further
     *
     * @param classDescription the OWLClassExpression currently under
     * consideration in String form
     * @param expressionToReplace OWLClassExpression in String form with all
     * disjunctive OWLClassExpressions
     * @param replacementExpression Single OWLClassExpression in String form to
     * replace the OWLClassExpression with all disjunctive OWLClassExpressions
     */
    private void createExpressionWithoutDisjunction(String classDescription, String expressionToReplace, String replacementExpression) {
        if (classDescription.indexOf(expressionToReplace) != -1) {
            String clsExp = classDescription.replaceFirst(Pattern.quote(expressionToReplace), Matcher.quoteReplacement(replacementExpression));
            if (!clsExp.equals("")) {
                //System.out.println("~~~~~~~~~~~~~~~~~~");
                //System.out.println(clsExp);
                //System.out.println();
                if (existDisjunctiveExpression(clsExp)) {
                    expressionsToProcess.push(clsExp);
                } else {
                    processedExpressions.push(clsExp);
                }
            } else {
                System.out.println("No expression found: Error");
            }
        }
    }

    /**
     * Return once an instance of OWLObjectUnionOf is found Loop through the set
     * of disjunctive OWLClassExpression and store them.
     *
     * @param desc the OWLClassExpression currently under consideration
     */
    private void generateDisjunctExpr(OWLClassExpression desc) {

        if (desc instanceof OWLObjectUnionOf) {
            currentUnionOfExpressions = r.render(desc).replaceAll("\\s+", " ");

            orClasses = new HashSet<OWLClassExpression>();
            if (!orClasses.isEmpty()) {
                orClasses.clear();
            }
            orClasses = desc.asDisjunctSet();
            System.out.println("THERE IS A UNION OF " + desc.asDisjunctSet().size() + " expressions");
            //for (OWLClassExpression cls : orClasses) {
            //  generateDisjunctExpr(cls);
            //}
            return;
        }

        if (desc instanceof OWLObjectIntersectionOf) {
            for (OWLClassExpression y : ((OWLObjectIntersectionOf) desc).getOperands()) {
                generateDisjunctExpr(y);
            }
        }

        if (desc instanceof OWLClass) {
            ((OWLClass) desc).getIRI().getFragment();
        }

        if (desc instanceof OWLObjectSomeValuesFrom) {
            generateDisjunctExpr(((OWLObjectSomeValuesFrom) desc).getFiller());
        }

        if (desc instanceof OWLObjectHasValue) {

        }

        if (desc instanceof OWLDataHasValue) {

        }

        if (desc instanceof OWLDataSomeValuesFrom) {

        }

    }

}
