/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse.entity.message.request;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import sse.entity.Service;

/**
 * An XML request for documentation suggestions.
 * @author mepcotterell
 */
@XmlRootElement(name = "documentationSuggestionRequest")
public class DocumentationSuggestionRequest {
    
    @XmlElement(name = "parameter", nillable = false)
    public String parameter;
    
    @XmlElement(name = "service", nillable = false)
    public Service service;

    /**
     * @param operation the operation to set
     */
    public void setService(Service service) {
        this.service = service;
    }
    
    public static void main(String[] args) {
        
        Service service = new Service();
        service.setDescriptionDocument("wublast.sawsdl");
        //service.setOntologyURI("webService.owl");
          
        DocumentationSuggestionRequest inputMessage = new DocumentationSuggestionRequest();
        inputMessage.setService(service);
        inputMessage.setParameter("program");
        
        try {
            JAXBContext  jc = JAXBContext.newInstance(DocumentationSuggestionRequest.class);
            StringWriter sw = new StringWriter();
            Marshaller   m  = jc.createMarshaller();
            
            m.marshal(inputMessage, sw);
            
            System.out.println(sw);
        } catch (JAXBException ex) {
            Logger.getLogger(DocumentationSuggestionRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><documentationInputMessage><parameter>program</parameter><service><descriptionDocument>wublast.sawsdl</descriptionDocument></service></documentationInputMessage>";
       
        try {
            
            ByteArrayInputStream input = new ByteArrayInputStream (xml.getBytes()); 
            JAXBContext          jc    = JAXBContext.newInstance(DocumentationSuggestionRequest.class);
            Unmarshaller         u     = jc.createUnmarshaller();
            
            DocumentationSuggestionRequest inputMessage2 = (DocumentationSuggestionRequest) u.unmarshal(input);
            
            System.out.println("inputMessage2.parameter = " + inputMessage2.parameter);
            System.out.println("inputMessage2.service.descriptionDocument = " + inputMessage2.service.descriptionDocument);
            System.out.println("inputMessage2.service.ontologyURI = " + inputMessage2.service.ontologyURI);
            
        } catch (JAXBException ex) {
            Logger.getLogger(DocumentationSuggestionRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    } // main

    /**
     * @param parameter the parameter to set
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    
}
