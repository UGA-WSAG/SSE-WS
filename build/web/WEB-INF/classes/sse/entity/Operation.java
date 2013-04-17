/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An operation in a Service
 * @author mepcotterell
 */
@XmlRootElement(name = "operation")
public class Operation {
    
    @XmlElement(name = "operationName", nillable = false)
    public String operationName;
    
    @XmlElement(name = "service", nillable = false)
    public Service service;
    
    @XmlElement(name = "note", nillable = false)
    public String note;

    /**
     * @param operationName the operationName to set
     */
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * @param service the service to set
     */
    public void setService(Service service) {
        this.service = service;
    }
    
    /**
     * @param service the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }
    
}
