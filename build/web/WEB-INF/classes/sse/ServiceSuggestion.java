/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sse;

import com.sun.jersey.spi.resource.Singleton;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import sse.entity.Operation;
import sse.entity.Service;
import sse.entity.SuggestedOperation;
import sse.entity.message.request.ServiceSuggestionRequest;
import sse.entity.message.response.ServiceSuggestionResponse;
import suggest.BackwardSuggest;
import suggest.BidirectionSuggest;
import suggest.ForwardSuggest;
import util.WebServiceOpr;
import util.WebServiceOprScore;

/**
 * REST Web Service for Service Suggestion Engine
 * This implementation is based on the JSR-311 specification.
 * @author mepcotterell
 */
@Path("serviceSuggestion")
@Singleton
public class ServiceSuggestion {

    protected static final String OBI_OWL = "http://purl.obolibrary.org/obo/obi.owl";
    protected static final String WEB_SERVICE_OWL = "http://obi-webservice.googlecode.com/svn/trunk/ontology/webService.owl";
    private static final Logger logger = Logger.getLogger(ServiceSuggestion.class.getName());
    
    @Context
    private ServletContext context;

    /**
     * Creates an error callback
     * @param error
     * @return 
     */
    private String wsExtensionsErrorJson (String error) {
        return String.format("$.wsextensions_error(\"The Service Suggestion Engine Web Service encountered an error on the server side. <pre>%s</pre>\");", error);
    } // wsExtensionsError
 
    private WebServiceOpr opFromEncodedString (String str) {
        
        String[] tokens = str.split("@");

        if (tokens.length == 2) {
            String opName  = tokens[0];
            String opUrl   = tokens[1];
            return new WebServiceOpr(opName, opUrl);
        } else if (tokens.length == 3) {
            String opName  = tokens[0];
            String opUrl   = tokens[1];
            String opExtra = tokens[2];
            return new WebServiceOpr(opName, opUrl, opExtra);
        } // if
        
        return null;
        
    } // opFromEncodedString
    
    
    @GET
    @Path("get/json/schema/request")
    @Produces("application/json")
    public String getSchemaRequestJSON () {
        String out = "";
        ObjectMapper mapper = new ObjectMapper();
        try { out += mapper.generateJsonSchema(ServiceSuggestionRequest.class); } catch (Exception ex) { }
        return out;
    }
    
    @GET
    @Path("get/json/schema/response")
    @Produces("application/json")
    public String getSchemaResponseJSON () {
        String out = "";
        ObjectMapper mapper = new ObjectMapper();
        try { out += mapper.generateJsonSchema(ServiceSuggestionResponse.class); } catch (Exception ex) { }
        return out;
    }
    
    @GET
    @Path("get/jsonp")
    @Produces("application/json")
    public String getJSONP (
        @QueryParam("payload")  String payload,
        @QueryParam("callback") String callback
    ) {
        
        logger.log(Level.INFO, "getJSONP operation invoked");
        
        // check the parameters
        if (payload == null) {
            logger.log(Level.WARNING, "getJSONP payload not included");
            return wsExtensionsErrorJson("no payload was included");
        } else if (callback == null) {
            logger.log(Level.WARNING, "getJSONP callback not specified");
            return wsExtensionsErrorJson("no callback function name was provided");
        } // if
        
        logger.log(Level.INFO, String.format("getJSONP received payload --> %s", payload));
        
        // attempt to unmarchal the payload
        ObjectMapper mapper = new ObjectMapper();
        ServiceSuggestionRequest request = null;
        
        try {
            request = mapper.readValue(payload, ServiceSuggestionRequest.class);
            logger.log(Level.INFO, "getJSONP successfully unmarchalled payload");
        } catch (Exception ex) {
            logger.log(Level.WARNING, String.format("getJSONP payload was malformed --> %s", payload));
            return wsExtensionsErrorJson("Bad request message format.");
        } // try
        
        // get the candidate ops
        List<WebServiceOpr> candidateOps = new ArrayList<WebServiceOpr>();
        for (Operation o : request.candidates) {
            candidateOps.add(new WebServiceOpr(o.operationName, o.service.descriptionDocument));
        }
        
        // get the workflow ops
        List<WebServiceOpr> workflowOps  = new ArrayList<WebServiceOpr>();
        for (Operation o : request.workflow) {
            workflowOps.add(new WebServiceOpr(o.operationName, o.service.descriptionDocument));
        }
        
        // get the workflow ops
        List<WebServiceOpr> workflowOps2  = new ArrayList<WebServiceOpr>();
        for (Operation o : request.workflow2) {
            workflowOps2.add(new WebServiceOpr(o.operationName, o.service.descriptionDocument));
        }
        
        // perform forward suggestion
        
        List<WebServiceOprScore> suggestOpList = null;
        
        try {
            if (request.direction.trim().equalsIgnoreCase("forward")) {
                ForwardSuggest sugg = new ForwardSuggest();
                suggestOpList = sugg.suggestNextService(workflowOps, candidateOps, request.desiredFunctionality, WEB_SERVICE_OWL, null);
            } else if (request.direction.trim().equalsIgnoreCase("backward")) {
                BackwardSuggest sugg = new BackwardSuggest();
                suggestOpList = sugg.suggestPrevServices(workflowOps2, candidateOps, request.desiredFunctionality, "", WEB_SERVICE_OWL, null);
            } else {
                BidirectionSuggest sugg = new BidirectionSuggest();
                suggestOpList = sugg.suggestServices(workflowOps, workflowOps2, candidateOps, request.desiredFunctionality, WEB_SERVICE_OWL, null);
            } // if
        } catch (Exception e) {
            logger.log(Level.WARNING, "getJSONP problem querying suggestion engine");
            return wsExtensionsErrorJson("problem querying suggestion engine");
        } // try
        
        logger.log(Level.INFO, "got suggested operations");
        
        List<SuggestedOperation> operations = new ArrayList<SuggestedOperation>();
        for (WebServiceOprScore o : suggestOpList) {
            
            Service s = new Service();
            s.setDescriptionDocument(o.getWsDescriptionDoc());
            
            SuggestedOperation so = new SuggestedOperation();
            so.setService(s);
            so.setOperationName(o.getOperationName());
            so.setScore(o.getScore());
            so.setDataMediationScore(o.getDmScore());
            so.setFunctionalityScore(o.getFnScore());
            so.setPreconditionEffectScore(o.getPeScore());
            
            // not the best way to do this...
            for (Operation op : request.candidates) {
                if ((op.service.descriptionDocument.compareTo(so.service.descriptionDocument) == 0) && (op.operationName.compareTo(so.operationName)) == 0) {
                    so.setNote(op.note);
                } // if
            } // for
            
            operations.add(so);
        } // for
        
        Collections.sort(operations);
        
        ServiceSuggestionResponse response = new ServiceSuggestionResponse();
        response.setOperations(operations);
        
        if (response.operations.isEmpty()) {
            response.error = true;
            response.note = "SSE crashed for some unknown reason";
        } // if
        
        // attemp to marchal the response
        String out = "";
        
        logger.log(Level.INFO, "sending response");
        
        try {
            out = String.format("%s(%s);", callback, mapper.writeValueAsString(response));
        } catch (Exception ex) {
            logger.log(Level.WARNING, "getJSONP problem marchalling response");
            return wsExtensionsErrorJson("problem marshalling response message"); 
        } // try
        
        // return the repsonse to the client
        return out;
        
    } // getJSONP
    
    @GET 
    @Path("get/json/{direction}")
    @Consumes("text/html")
    @Produces("application/json")
    public String getJSON (
        @PathParam("direction")   String direction,
        @QueryParam("desired")    String desired,
        @QueryParam("candidates") String candidates,
        @QueryParam("workflow")   String workflow,
        @QueryParam("callback")   String callback
    ) {
        
        logger.log(Level.INFO, "getSuggestionsJSONP operation invoked.");
        
        if (candidates == null) {
            return wsExtensionsErrorJson("Query paramter 'candidates' required for this operation.");
        } // if
        
        if (workflow == null) {
            return wsExtensionsErrorJson("Query paramter 'workflow' required for this operation.");
        } // if
        
        if (callback == null) {
            return wsExtensionsErrorJson("Query paramter 'callback' required for this operation.");
        } // if
        
        // create a JSONArray object to store the suggestions
        JSONArray suggestions = new JSONArray();
        
        // A list to hold the candidate operations
        List<WebServiceOpr> candidateOps = new ArrayList<WebServiceOpr>();
        
        if (candidates != null) {
            
           StringTokenizer opTokens = new StringTokenizer(candidates, ",");
           
           while (opTokens.hasMoreTokens()) {
               
               String next = opTokens.nextToken();
               WebServiceOpr op = opFromEncodedString(next);
               
               if (op != null) {
                   candidateOps.add(op);
               } // if
           
           } // while
           
        } // if
        
        // A list to hold the workflow operations
        List<WebServiceOpr> workflowOps = new ArrayList<WebServiceOpr>();
        
        if (workflow != null) {
            
           StringTokenizer opTokens = new StringTokenizer(workflow, ",");
           
           while (opTokens.hasMoreTokens()) {
               
               String next = opTokens.nextToken();
               WebServiceOpr op = opFromEncodedString(next);
               
               if (op != null) {
                   workflowOps.add(op);
               } // if
           
           } // while
           
        } // if
        
        // Filter out stuff from the candidates that already exist in the workflow
        List <WebServiceOpr> toDelete = new ArrayList<WebServiceOpr>();
        for (WebServiceOpr opA: candidateOps) {
            for (WebServiceOpr opB: workflowOps) {
                if (opA.compareTo(opB)) {
                    toDelete.add(opA);
                } // if
            } // for
        } // for
        candidateOps.removeAll(toDelete);
        
        ForwardSuggest sugg = new ForwardSuggest();
        List<WebServiceOprScore> suggestOpList = sugg.suggestNextService(workflowOps, candidateOps, desired, this.context.getRealPath("WEB-INF/owl/obi.owl"), null);
        
        DecimalFormat df = new DecimalFormat("0.000");
        
        for (WebServiceOprScore score: suggestOpList){
            String [] suggestion = {
                score.getOperationName(), 
                score.getWsDescriptionDoc(), 
                df.format(score.getScore()),
                df.format(score.getDmScore()),
                df.format(score.getFnScore()),
                df.format(score.getPeScore()),
                score.getExtraInfo()
            };
            suggestions.put(suggestion);
        } // for
        
        // return the JSON array wrapped with the callback function
        if (callback == null) {
            return String.format("%s", callback, suggestions.toString());
        } else {
            return String.format("%s(%s);", callback, suggestions.toString());
        }
        
    } // getSuggestionsJsonp
    
    @POST 
    @Path("get/xml/{direction}")
    @Consumes({"application/xml", "text/xml"})
    @Produces({"application/xml", "text/xml"})
    public ServiceSuggestionResponse getXML (
        @PathParam("direction")   String direction,
        ServiceSuggestionRequest  request
    ) {
        
        List<WebServiceOpr> candidateOps = new ArrayList<WebServiceOpr>();
        for (Operation o : request.candidates) {
            candidateOps.add(new WebServiceOpr(o.operationName, o.service.descriptionDocument));
        }
        
        List<WebServiceOpr> workflowOps  = new ArrayList<WebServiceOpr>();
        for (Operation o : request.workflow) {
            workflowOps.add(new WebServiceOpr(o.operationName, o.service.descriptionDocument));
        }
        
        ForwardSuggest sugg = new ForwardSuggest();
        List<WebServiceOprScore> suggestOpList = sugg.suggestNextService(workflowOps, candidateOps, request.desiredFunctionality, this.context.getRealPath("WEB-INF/owl/obi.owl"), null);
        
        List<SuggestedOperation> operations = new ArrayList<SuggestedOperation>();
        for (WebServiceOprScore o : suggestOpList) {
            
            Service s = new Service();
            s.setDescriptionDocument(o.getWsDescriptionDoc());
            
            SuggestedOperation so = new SuggestedOperation();
            so.setService(s);
            so.setOperationName(o.getOperationName());
            so.setScore(o.getScore());
            so.setDataMediationScore(o.getDmScore());
            so.setFunctionalityScore(o.getFnScore());
            so.setPreconditionEffectScore(o.getPeScore());
            
            operations.add(so);
        }
        
        Collections.sort(operations);
        
        ServiceSuggestionResponse response = new ServiceSuggestionResponse();
        response.setOperations(operations);
        
        return response;
        
    } // getXML
    
} // ServiceSuggestion
