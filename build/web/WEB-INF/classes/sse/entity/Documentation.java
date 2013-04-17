/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse.entity;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Suggested Documentation
 * @author mepcotterell
 */
@XmlRootElement(name = "documentation")
public class Documentation {
    
    @XmlElement(name = "doc", nillable = false)
    private List<String> docs;

    /**
     * @param docs the docs to set
     */
    public void setDocs(List<String> docs) {
        this.docs = docs;
    }
    
} // Documentation
