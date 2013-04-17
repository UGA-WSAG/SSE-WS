/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse.entity.message.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import sse.entity.Service;

/**
 * An XML request message for parameter value suggestions
 * @author mepcotterell
 */
@XmlRootElement(name = "parameterValueSuggestionRequest")
public class ParameterValueSuggestionRequest {
    
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

    /**
     * @param parameter the parameter to set
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    
}
