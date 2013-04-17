/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Service
 * @author mepcotterell
 */
@XmlRootElement(name = "service")
public class Service {
    
    @XmlElement(name = "descriptionDocument", nillable = false)
    public String descriptionDocument;
    
    @XmlElement(name = "ontologyURI", nillable = true)
    public String ontologyURI;

    /**
     * @param descriptionDocument the descriptionDocument to set
     */
    public void setDescriptionDocument(String descriptionDocument) {
        this.descriptionDocument = descriptionDocument;
    }

    /**
     * @param ontologyURI the ontologyURI to set
     */
    public void setOntologyURI(String ontologyURI) {
        this.ontologyURI = ontologyURI;
    }
    
} // Service
