/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse.entity.message.response;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An XML response for parameter value suggestions
 * @author mepcotterell
 */
@XmlRootElement(name = "parameterValueSuggestionResponse")
public class ParameterValueSuggestionResponse {
    
    @XmlElement(name = "value", nillable = false)
    public List<String> values;

    /**
     * @param parameters the parameters to set
     */
    public void setValues(List<String> values) {
        this.values = values;
    }
    
} // ParameterValueSuggestionResponse
