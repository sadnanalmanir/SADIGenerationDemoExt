package org.ruleml.psoa.restful.resources;

import org.jboss.resteasy.logging.Logger;
import org.ruleml.psoa.restful.models.URILoadRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by sadnana on 24/03/16.
 */

@Path("/registerservice")
public class ServiceRegistrationLoader {


    Logger logger = Logger.getLogger(ServiceRegistrationLoader.class);
    @Context
    UriInfo info;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded


    public String registerSADIService(URILoadRequest uriLoadRequest) {

        String completeRegistryServiceURI = decode(uriLoadRequest.getIri());
        String command = "curl " + completeRegistryServiceURI;

        logger.info("Complete registry path for the service : " + command);

        String response = executeCommand(command);
        logger.info("Response from service registration : \n" + response);

        return "success";
    }

    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    private static String decode(String s) {
        return URLDecoder.decode(s.replace("&gt;", ">"));
        /*String result = s;
        try {
            result = URLEncoder.encode(s.replace("&gt;", ">"), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;*/
    }
}
