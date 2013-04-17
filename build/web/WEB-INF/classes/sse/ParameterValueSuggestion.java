/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse;

import com.sun.jersey.spi.resource.Singleton;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.json.JSONArray;
import sse.entity.message.request.ParameterValueSuggestionRequest;
import sse.entity.message.response.ParameterValueSuggestionResponse;
import workflowHelp.SuggestInputValues;

/**
 * REST Web Service
 *
 * @author mepcotterell
 */
@Path("parameterValueSuggestion")
@Singleton
public class ParameterValueSuggestion {

    private static final Logger logger = Logger.getLogger(ServiceSuggestion.class.getName());
    
    @Context
    private UriInfo uContext;
    
    @Context
    private ServletContext sContext;

    /**
     * Retrieves representation of an instance of sse.InputValueSuggestion
     * @return an instance of java.lang.String
     */
    @GET
    @Path("get/json")
    @Consumes("text/html")
    @Produces("application/json")
    public String getJSON(
        @QueryParam("wsdl")     String wsdl,
        @QueryParam("param")    String param,
        @QueryParam("callback") String callback
    ) {
        
        logger.log(Level.INFO, "ParameterValueSuggestion.getJSONP operation invoked.");
        logger.log(Level.INFO, "wsdl = {0}, param = {1}, callback = {2}", new String[] {wsdl, param, callback});
        
        List<String> inputs      = SuggestInputValues.SuggestParamValues(wsdl, param, ServiceSuggestion.OBI_OWL);
        JSONArray    suggestions = new JSONArray();
        
        for (String input : inputs) {
            suggestions.put(input);
        } // for
        
        if (callback == null) {
            return String.format("%s", suggestions.toString());
        } else {
            return String.format("%s(%s);", callback, suggestions.toString());
        }
    } // getJSON
    
    /**
     * Retrieves representation of an instance of sse.InputValueSuggestion
     * @return an instance of java.lang.String
     */
    @POST
    @Path("get/xml")
    @Consumes({"application/xml", "text/xml"})
    @Produces({"application/xml", "text/xml"})
    public ParameterValueSuggestionResponse getXML(ParameterValueSuggestionRequest inputMessage) {
        
        logger.log(Level.INFO, "wsdl = {0}, param = {1}, ontologuURI = {2}", new String[] {inputMessage.service.descriptionDocument, inputMessage.parameter, inputMessage.service.ontologyURI});
        
        String ontologyPath = inputMessage.service.ontologyURI;
       
        if (ontologyPath == null) {
            ontologyPath = ServiceSuggestion.OBI_OWL;
        }
        
        List<String> values = SuggestInputValues.SuggestParamValues(inputMessage.service.descriptionDocument, inputMessage.parameter, ontologyPath);
        
        ParameterValueSuggestionResponse outputMessage = new ParameterValueSuggestionResponse();
        outputMessage.setValues(values);
        
        logger.log(Level.INFO, "Returning results.");
        
        return outputMessage;
    } // getXML

}
