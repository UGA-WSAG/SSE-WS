/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse.entity.message.response;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import sse.entity.Documentation;

/**
 * An XML response for documentation suggestions
 * @author mepcotterell
 */
@XmlRootElement(name = "documentationSuggestionResponse")
public class DocumentationSuggestionResponse {
    
    @XmlElement(name = "documentation", nillable = false)
    private Documentation documentation = null;

    /**
     * @param documentation the documentation to set
     */
    public void setDocumentation(Documentation documentation) {
        this.documentation = documentation;
    }
    
    public static void main(String[] args) {
        
        List<String> docs = new ArrayList<String>();
        
        docs.add("documentation string 1");
        docs.add("documentation string 2");
        
        Documentation documentation = new Documentation();
        documentation.setDocs(docs);
        
        DocumentationSuggestionResponse outputMessage = new DocumentationSuggestionResponse();
        outputMessage.setDocumentation(documentation);
        
        try {
            JAXBContext  jc = JAXBContext.newInstance(DocumentationSuggestionResponse.class);
            StringWriter sw = new StringWriter();
            Marshaller   m  = jc.createMarshaller();
            
            m.marshal(outputMessage, sw);
            
            System.out.println(sw);
        } catch (JAXBException ex) {
            Logger.getLogger(DocumentationSuggestionResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    } // main
    
} 