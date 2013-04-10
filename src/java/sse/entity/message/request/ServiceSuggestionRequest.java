/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse.entity.message.request;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.ObjectMapper;
import sse.entity.Operation;
import sse.entity.Service;

/**
 * An XML request for service suggestions
 * @author mepcotterell
 */
@XmlRootElement(name = "serviceSuggestionRequest")
public class ServiceSuggestionRequest {
    
    @XmlElement(name = "direction", nillable = false)
    public String direction;
    
    @XmlElement(name = "desiredFunctionality", nillable = true)
    public String desiredFunctionality;
    
    @XmlElementWrapper(name="candidates")
    @XmlElement(name = "operation", nillable = false)
    public List<Operation> candidates;
    
    @XmlElementWrapper(name="workflow")
    @XmlElement(name = "operation", nillable = false)
    public List<Operation> workflow;
    
    @XmlElementWrapper(name="workflow2")
    @XmlElement(name = "operation", nillable = false)
    public List<Operation> workflow2;

    /**
     * @param direction the direction to set
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    /**
     * @param desiredFunctionality the desiredFunctionality to set
     */
    public void setDesiredFunctionality(String desiredFunctionality) {
        this.desiredFunctionality = desiredFunctionality;
    }

    /**
     * @param candiates the candiates to set
     */
    public void setCandidates(List<Operation> candiates) {
        this.candidates = candiates;
    }

    /**
     * @param workflow the workflow to set
     */
    public void setWorkflow(List<Operation> workflow) {
        this.workflow = workflow;
    }
    
    /**
     * @param workflow the second workflow to set
     */
    public void setWorkflow2(List<Operation> workflow2) {
        this.workflow2 = workflow2;
    }
    
    public static void main(String[] args) {
        
        Service s = new Service();
        s.setDescriptionDocument("wublast.sawsdl");
        
        Operation op1 = new Operation();
        op1.setOperationName("run");
        op1.setService(s);
        
        List<Operation> candidates = new ArrayList<Operation>();
        candidates.add(op1);
        
        List<Operation> workflow = new ArrayList<Operation>();
        workflow.add(op1);
        
        ServiceSuggestionRequest request = new ServiceSuggestionRequest();
        request.setCandidates(candidates);
        request.setWorkflow(workflow);
        
        try {
            JAXBContext  jc = JAXBContext.newInstance(ServiceSuggestionRequest.class);
            StringWriter sw = new StringWriter();
            Marshaller   m  = jc.createMarshaller();
            
            m.marshal(request, sw);
            
            System.out.println(sw);
        } catch (JAXBException ex) {
            Logger.getLogger(ServiceSuggestionRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(request);
        
    } // main
   
    @Override
    public String toString() {
        
        String out = "request.toString -> ";
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            out += mapper.writeValueAsString(this);
        } catch (Exception ex) {
            
        }
        
        return out;
    } // toString
    
}
