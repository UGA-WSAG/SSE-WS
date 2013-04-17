/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse.entity.message.response;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import sse.entity.Service;
import sse.entity.SuggestedOperation;

/**
 * An XML response to service suggestions
 * @author mepcotterell
 */
@XmlRootElement(name = "serviceSuggestionResponse")
public class ServiceSuggestionResponse {
   
    @XmlElementWrapper(name="operations")
    @XmlElement(name = "operation", nillable = false)
    public List<SuggestedOperation> operations;

    @XmlElementWrapper(name="error")
    @XmlElement(name = "error", nillable = false)
    public boolean error = false;
    
    @XmlElementWrapper(name="note")
    @XmlElement(name = "note", nillable = false)
    public String note = "";
    
    /**
     * @param operations the operations to set
     */
    public void setOperations(List<SuggestedOperation> operations) {
        this.operations = operations;
    }
    
    public static void main (String[] args) {
        
        Service s = new Service();
        s.setDescriptionDocument("weublast.sawsdl");
        
        SuggestedOperation op1 = new SuggestedOperation();
        op1.setOperationName("run");
        op1.setService(s);
        op1.setDataMediationScore(0.5);
        op1.setFunctionalityScore(0.2);
        op1.setScore(0.7);
        
        SuggestedOperation op2 = new SuggestedOperation();
        op2.setOperationName("testOp");
        op2.setService(s);
        op2.setDataMediationScore(0.7);
        op2.setFunctionalityScore(0.3);
        op2.setScore(1.0);
        
        List<SuggestedOperation> operations = new ArrayList<SuggestedOperation>();
        operations.add(op1);
        operations.add(op2);
        
        Collections.sort(operations);
        
        ServiceSuggestionResponse response = new ServiceSuggestionResponse();
        response.setOperations(operations);
        
        try {
            JAXBContext  jc = JAXBContext.newInstance(ServiceSuggestionResponse.class);
            StringWriter sw = new StringWriter();
            Marshaller   m  = jc.createMarshaller();
            
            m.marshal(response, sw);
            
            System.out.println(sw);
        } catch (JAXBException ex) {
            Logger.getLogger(ServiceSuggestionResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
} // ServiceSuggestionResponse
