package org.ruleml.psoa.restful.resources;


import ca.unbsj.cbakerlab.codegenerator.GenerateSADIService;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ruleml.psoa.restful.models.ServiceParameterRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URLDecoder;

/**
 * Created by sadnana on 05/03/16.
 */

@Path("/viewsourcecode")

public class SADICodeGeneratorRunner {

    GenerateSADIService sadiService;

    public SADICodeGeneratorRunner() throws MojoFailureException, MojoExecutionException {

    }

    private static String decode(String s) {
        return URLDecoder.decode(s.replace("&gt;", ">"));
    }

    @Path("/serviceparameters")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Encoded

    /* was void before, just sending a string to ajax handler to make sure that
       the code is populated AFTER the generation is performed, otherwise there is a
       propagation delay and the population doesn't show up instantly.
      */
    public String loadServiceParameters(ServiceParameterRequest request) throws MojoFailureException, MojoExecutionException {

        this.sadiService = new GenerateSADIService(decode(request.getServiceName()),
                decode(request.getServiceClass()),
                decode(request.getServiceInputURI()),
                        decode(request.getServiceOutputURI()),
                                decode(request.getServiceDescription()),
                                        decode(request.getServiceEmail()));
        this.sadiService.execute();

        return "Code generated.";
    }

    @Path("/sadiserviceclass")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded
    public String getServiceClassSourceCode() {
        if (!sadiService.getServiceClassContent().isEmpty())
            return sadiService.getServiceClassContent();
        else
            return "No code generated for service class.";
    }

    @Path("/dbconnectionclass")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded
    public String getDBConnClassSourceCode() {
        if (!sadiService.getMysqlDBClassContent().isEmpty())
            return sadiService.getMysqlDBClassContent();
        else
            return "No code generated for DB Connection class.";
    }

    @Path("/webxmlconf")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded
    public String getWebXMLConfCode() {
        if (!sadiService.getWebXMLContent().isEmpty())
            return sadiService.getWebXMLContent();
        else
            return "No code generated for web.xml Configuration.";
    }

    @Path("/indexjsp")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded
    public String getIndexJSPSourceCode() {
        if (!sadiService.getIndexJSPContent().isEmpty())
            return sadiService.getIndexJSPContent();
        else
            return "No code generated for index.jsp.";
    }

    //try {
        //sadiService.
    //} catch (MojoExecutionException e) {
    //    e.printStackTrace();
    //} catch (MojoFailureException e) {
    //    e.printStackTrace();
    //}

    @Path("/pomxml")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded
    public String getPomXMLConf() {
        if (!sadiService.getPomXMLContent().isEmpty())
            return sadiService.getPomXMLContent();
        else
            return "No code generated for pom.xml.";
    }

}
