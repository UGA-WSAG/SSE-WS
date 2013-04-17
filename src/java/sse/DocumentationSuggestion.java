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
import sse.entity.Documentation;
import sse.entity.message.request.DocumentationSuggestionRequest;
import sse.entity.message.response.DocumentationSuggestionResponse;
import workflowHelp.GetDocumentation;

/**
 * REST Web Service for Documentation Suggestion
 * @author mepcotterell
 */
@Path("documentationSuggestion")
@Singleton
public class DocumentationSuggestion {

    private static final Logger logger = Logger.getLogger(ServiceSuggestion.class.getName());
    
    @Context
    private UriInfo uContext;
    
    @Context
    private ServletContext sContext;
    
    /**
     * Returns documentation suggestions in JSON format.
     * If a callback is specified the the output is formatted in JSONP.
     * @param descriptionDocument The service description document used to annotate
     * @param parameter The paramater for which documentation was requested
     * @param callback An optional String used as the JSONP callback function
     * @return 
     */
    @GET
    @Path("get/json")
    @Consumes("text/html")
    @Produces("application/json")
    public String getJSON(
        @QueryParam("wsdl")     String descriptionDocument,
        @QueryParam("param")    String parameter,
        @QueryParam("callback") String callback
    ) {
        
        logger.log(Level.INFO, "Documentation.getJSONP operation invoked.");
        logger.log(Level.INFO, "wsdl = {0}, param = {1}, callback = {2}", new String[] {descriptionDocument, parameter, callback});
        
        List<String> docs        = GetDocumentation.getParamInfo(descriptionDocument, parameter, ServiceSuggestion.WEB_SERVICE_OWL);
        JSONArray    suggestions = new JSONArray();
        
        for (String doc : docs) {
            suggestions.put(doc);
        } // for
        
        logger.log(Level.INFO, "Returning results.");
        if (callback == null) {
            return String.format("%s", suggestions.toString());
        } else {
            if (callback.trim().isEmpty()) {
                return String.format("%s", suggestions.toString());
            } else {
                return String.format("%s(%s);", callback, suggestions.toString());
            }
        }
    } // getJSONP
    
    /**
     * Retrieves representation of an instance of sse.InputValueSuggestion
     * @return an instance of java.lang.String
     */
    @POST
    @Path("get/xml")
    @Consumes({"application/xml", "text/xml"})
    @Produces({"application/xml", "text/xml"})
    public DocumentationSuggestionResponse getXML(DocumentationSuggestionRequest inputMessage) {
        
        logger.log(Level.INFO, "wsdl = {0}, param = {1}, ontologuURI = {2}", new String[] {inputMessage.service.descriptionDocument, inputMessage.parameter, inputMessage.service.ontologyURI});
        
        String ontologyPath = inputMessage.service.ontologyURI;
       
        if (ontologyPath == null) {
            ontologyPath = ServiceSuggestion.WEB_SERVICE_OWL;
        }
        
        List<String> docs        = GetDocumentation.getParamInfo(inputMessage.service.descriptionDocument, inputMessage.parameter, ontologyPath);
      
        Documentation documentation = new Documentation();
        documentation.setDocs(docs);
        
        DocumentationSuggestionResponse outputMessage = new DocumentationSuggestionResponse();
        outputMessage.setDocumentation(documentation);
        
        logger.log(Level.INFO, "Returning results.");
        
        return outputMessage;
    } // getXML

}
