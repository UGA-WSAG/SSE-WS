/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Suggested Operation
 * @author mepcotterell
 */
@XmlRootElement(name = "suggestedOperation")
public class SuggestedOperation extends Operation implements Comparable<SuggestedOperation> {
    
    @XmlElement(name = "score", nillable = false)
    public double score;
    
    @XmlElement(name = "dataMediationScore", nillable = false)
    public double dataMediationScore;
    
    @XmlElement(name = "functionalityScore", nillable = false)
    public double functionalityScore;
    
    @XmlElement(name = "preconditionEffectScore", nillable = true)
    public double preconditionEffectScore;

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * @param dataMediationScore the dataMediationScore to set
     */
    public void setDataMediationScore(double dataMediationScore) {
        this.dataMediationScore = dataMediationScore;
    }

    /**
     * @param functionalityScore the functionalityScore to set
     */
    public void setFunctionalityScore(double functionalityScore) {
        this.functionalityScore = functionalityScore;
    }

    /**
     * @param preconditionEffectScore the preconditionEffectScore to set
     */
    public void setPreconditionEffectScore(double preconditionEffectScore) {
        this.preconditionEffectScore = preconditionEffectScore;
    }

    @Override
    public int compareTo(SuggestedOperation t) {
        // reverse ordering
        return (new Double(t.score)).compareTo(score);
    } // compareTo
    
} // SuggestedOperation
