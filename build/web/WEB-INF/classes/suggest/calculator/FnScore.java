package suggest.calculator;

import java.util.logging.Level;
import java.util.logging.Logger;
import ontologyManager.OntologyManager;
import ontologySimilarity.ConceptSimilarity;
import org.semanticweb.owlapi.model.OWLClass;
import parser.SawsdlParser;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import util.WebServiceOpr;

/**
 * 
 * Class provides methods to calculate FnScore component of Forward/Backward or Bidirectional Suggest
 * @author Rui Wang 
 * @author Alok Dhamanaskar
 * @see LICENSE (MIT style license file).
 * 
 * 
 */
public class FnScore
{
    /**
     * Calculates and return FnScore depending upon how well the desiredFn (keywords/Concept) 
     * aligns with the objective specification of the WebService Operation passed to it. 
     * 
     * @param desiredFn   The desired Functionality entered by the user as a URI of the concept in the Ontology
     * @param op          The WebService operation for which the FnScore has to be calculated
     * @param owlFileName URI of the Ontology file
     * @return            FnScore as double value
     */
    public double calculateFnScore(String desiredFn, WebServiceOpr op, String owlFileName)
    {
        double fnScore = 0;
        String owlURI = owlFileName;
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        //penality for only syntax match
        double penality = 0.7;
        if (desiredFn == null || desiredFn.trim().equals("") || op == null )
            return fnScore;

        SawsdlParser sawsdl = new SawsdlParser();
        String oprModelRefr = sawsdl.getOpModelreference(op.getOperationName(), op.getWsDescriptionDoc());
        
        if(oprModelRefr == null || oprModelRefr.trim().equals(""))
        {
            //ModelReference Is NOT Present, therefore use OperationName from WSDL
            String operationName = op.getOperationName();
            if(!desiredFn.startsWith("http://"))
            {
                //Desired Funcionality is Specified as Key Words
                QGramsDistance mc = new QGramsDistance();
                fnScore = penality * mc.getSimilarity(desiredFn, operationName);
            }
            else
            {
                //Desired Functionality is specified as Concept URI
                OWLClass preferopClass = parser.getConceptClass(desiredFn);
                QGramsDistance mc = new QGramsDistance();
                if (preferopClass != null)
                    fnScore = penality * mc.getSimilarity(parser.getClassLabel(preferopClass), operationName);
                else
                {
                    String errMsg = "The concept URI doesnt seem to exist: "+ desiredFn +" provided as desired functionality ";
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, errMsg);
                }
            }    
        }//if ends
        else
        {
            //ModelReference is specified as a concept in the Ontology
            if(!desiredFn.startsWith("http://"))
            {
                //Desired Funcionality is Specified as Key Words
                OWLClass MrClass = parser.getConceptClass(oprModelRefr);
                QGramsDistance mc = new QGramsDistance();
                if (MrClass != null)
                {
                   String label = parser.getClassLabel(MrClass);
                   fnScore = penality * mc.getSimilarity(desiredFn, label);
                }
                else
                {
                    String errMsg = "The concept URI doesnt seem to exist: "+ oprModelRefr +" found in "+ op.getWsDescriptionDoc();
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, errMsg);
                }
            }
            else
            {
                //Desired Functionality is specified as Concept URI
                if (oprModelRefr.equalsIgnoreCase(desiredFn))
                    return 1;
                OWLClass MrClass = parser.getConceptClass(oprModelRefr);
                OWLClass preferopClass = parser.getConceptClass(desiredFn);
                if( MrClass != null && preferopClass != null)
                    fnScore = ConceptSimilarity.getConceptSimScore(MrClass, preferopClass, owlURI);
                else
                {
                    String errMsg = "One of the concepts doesnt seem to exist: "+ oprModelRefr +" found in "+ op.getWsDescriptionDoc() +" or "+ desiredFn;
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, errMsg);
                }
             }
        }       
        return fnScore;
    }// calculateFnScore ends

    public static void main(String[] args)
    {
        //Test Code, To test it idependently of Other scores
        String desiredFn = "pairwise sequence alignment";
        String OWLURI = "owl/webService.owl";
        WebServiceOpr opr = new WebServiceOpr("run", "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wublast.sawsdl");
        
        FnScore fn =  new FnScore();
        System.out.println("FnScore =  "+fn.calculateFnScore(desiredFn, opr, OWLURI));

        desiredFn = "http://purl.obolibrary.org/obo/OBI_0200081";
        System.out.println("\n\nFnScore =  "+fn.calculateFnScore(desiredFn, opr, OWLURI));
        
        opr = new WebServiceOpr("getFormatStyles", "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/WSDbfetch.sawsdl");
        desiredFn = "http://purl.obolibrary.org/obo/OBI_0200081";
        System.out.println("\n\nFnScore =  "+fn.calculateFnScore(desiredFn, opr, OWLURI));        
        
    }
}
