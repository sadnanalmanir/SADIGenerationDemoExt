package org.ruleml.psoa.restful.resources;

import org.apache.log4j.Logger;
import org.ruleml.psoa.restful.models.URILoadRequest;
import org.sadiframework.beans.ServiceBean;
import org.sadiframework.client.Service;
import org.sadiframework.client.ServiceConnectionException;
import org.sadiframework.client.ServiceFactory;
import org.sadiframework.client.ServiceImpl;
import org.sadiframework.registry.Registry;
import org.sadiframework.service.validation.ServiceValidator;
import org.sadiframework.service.validation.ValidationResult;

import javax.ws.rs.Encoded;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.net.URLDecoder;
import java.util.Collection;

/**
 * Created by sadnana on 26/03/16.
 */

import org.apache.log4j.Logger;
        import org.sadiframework.SADIException;
        import org.sadiframework.beans.ServiceBean;
        import org.sadiframework.client.Service;
        import org.sadiframework.client.ServiceConnectionException;
        import org.sadiframework.client.ServiceFactory;
        import org.sadiframework.client.ServiceImpl;
        import org.sadiframework.registry.*;
//import org.sadiframework.registry.utils.Twitter;
        import org.sadiframework.service.validation.*;

        import javax.ws.rs.*;
        import javax.ws.rs.core.Context;
        import javax.ws.rs.core.MediaType;
        import javax.ws.rs.core.UriInfo;
        import java.util.Collection;


@Path("/registry")
public class SADIServiceRegistration {

    boolean doValidate;
    boolean doRegister;
    boolean doTweet;

    public SADIServiceRegistration(){
        doValidate = false;
        doRegister = true;
        doTweet = false;

    }
    org.jboss.resteasy.logging.Logger logger = org.jboss.resteasy.logging.Logger.getLogger(SADIServiceRegistration.class);
    @Context
    UriInfo info;

    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded

    public String completeRegistration(URILoadRequest uriLoadRequest){

        String serviceURI = decode(uriLoadRequest.getIri());
        //String serviceURI = "http://cbakerlab.unbsj.ca:8080/getDiagnosisCodeByDiagnosisID_demo/getDiagnosisCodeByDiagnosisID";
        if (serviceURI != null) {
            Logger log = Logger.getLogger("org.sadiframework.registry");
            Registry registry = null;
            try {
                registry = Registry.getRegistry();

                Service service = null;
                try {
                    service = ServiceFactory.createService(serviceURI);
                } catch (ServiceConnectionException e) {
                    if (registry.containsService(serviceURI)) {
                        doValidate = false;
                        doRegister = false;
                        registry.unregisterService(serviceURI);

                        ServiceBean serviceBean = new ServiceBean();
                        serviceBean.setURI(serviceURI);
                        //request.setAttribute("service", serviceBean);
                        //request.setAttribute("unregister", true);
                    } else {
                        throw e;
                    }
                } // other exceptions thrown to outer...


                if (doValidate) {

                    ValidationResult result = ServiceValidator.validateService(((ServiceImpl)service).getServiceModel().getResource(serviceURI));
                    //request.setAttribute("service", result.getService());
                    //request.setAttribute("warnings", result.getWarnings());
                    if (!result.getWarnings().isEmpty()) {
                        doRegister = false;
                    }
                }

                if (doRegister) {
                    doTweet &= !registry.containsService(serviceURI); // only tweet new services
                    ServiceBean serviceBean = registry.registerService(serviceURI);
                    //request.setAttribute("service", serviceBean);
                    if (doTweet) {
                        try {
                            //Twitter.tweetService(serviceBean);
                            logger.info("tweeting new service registration-- will be omitted.");
                        } catch (final Exception e) {
                            log.error(String.format("error tweeting registration of %s: %s", serviceURI, e));
                        }
                    }
                }
            } catch (Exception e) {
                log.error(String.format("registration failed for %s: %s", serviceURI, e.getMessage()), e);
                ServiceBean service = new ServiceBean();
                service.setURI(serviceURI);
                //request.setAttribute("service", service);
                //request.setAttribute("error", e.getMessage() != null ? e.getMessage() : e.toString());
            } finally {
                if (registry != null)
                    registry.getModel().close();
            }


            logger.info("methods called for registration");
        }



        return "registration successful";
    }




    @Path("/services")
    @POST
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded
    public String displayServices(){


        Registry registry = null;
        String SADIServiceDescriptions = "";
        int serviceCounter = 0;
        try {
            registry = Registry.getRegistry();
            //pageContext.setAttribute("services", registry.getRegisteredServices());
            Collection<ServiceBean> services = registry.getRegisteredServices();
            serviceCounter = services.size();
            SADIServiceDescriptions += "<h3><b>"+ "Registered Services Found: "+ serviceCounter +"</b></h3>";
            SADIServiceDescriptions += "<hr>";
            SADIServiceDescriptions += "<div class=\"card\">";


            for (ServiceBean service : services) {
                System.out.println(service.getURI());
                System.out.println(service.getInputClassURI());
                System.out.println(service.getOutputClassURI());


                SADIServiceDescriptions +=
                    "<table class=\"table\">\n" +
                            "    <thead>\n" +
                            "      <tr>\n" +
                            "        <th>" + "<h3>" + service.getName() + "</h3>" + "</th>        \n" +
                            "      </tr>\n" +
                            "    </thead>\n" +
                            "    <tbody>\n" +
                            "      <tr class=\"success\">\n" +
                            "        <td class=\"col-sm-4\"><b>Description</b></td>\n" +
                            "        <td class=\"col-sm-8\">" + service.getDescription() + "</td>\n" +
                            "      </tr>\n" +
                            "      <tr class=\"success\">\n" +
                            "        <td class=\"col-sm-4\"><b>Service URL</b></td>\n" +
                            "        <td class=\"col-sm-8\"><a href=\">" + service.getURI() + "\">" + service.getURI() + "</a>" + "</td>\n" +
                            "      </tr>\n" +
                            "      <tr class=\"danger\">\n" +
                            "        <td class=\"col-sm-4\"><b>Input Class</b></td>\n" +
                            "        <td class=\"col-sm-8\"><a href=\">" + service.getInputClassURI() + "\">" + service.getInputClassURI() + "</a>" + "</td>\n" +
                            "      </tr>\n" +
                            "      <tr class=\"info\">\n" +
                            "        <td class=\"col-sm-4\"><b>Output Class</b></td>\n" +
                            "        <td class=\"col-sm-8\"><a href=\">" + service.getOutputClassURI() + "\">" + service.getOutputClassURI() + "</a>" + "</td>\n" +
                            "      </tr>\n" +
                            "    </tbody>\n" +
                            "  </table>";



                /*
                SADIServiceDescriptions +=
                        "<div class=\"card-header\">"+ service.getName() + "</div>"
                        + "<div class=\"card-block\">"
                        +   "<p class=\"card-text\">"
                        +     "<br/><b>Service URL: <b><a class=\"label label-info\" href=\'"+ service.getURI() + "\' title=\'" + service.getURI() + "\'>" + service.getURI() + "</a>"
                        +     "<br/><b>Input Class: <b><a class=\"label label-info\" href=\'"+ service.getInputClassURI() + "\' title=\'" + service.getInputClassURI() + "\'>" + service.getInputClassURI() + "</a>"
                        +     "<br/><b>Output Class: <b><a class=\"label label-info\" href=\'"+ service.getOutputClassURI() + "\' title=\'" + service.getOutputClassURI() + "\'>" + service.getOutputClassURI() + "</a>"
                        +   "</p>"
                        +"</div>";
                        */
                /*
                SADIServiceDescriptions += "<span class=\"label label-primary\">"+ "Services Found: "+ serviceCounter +"</span>" +
                        "<br/><b>Service URL: <b><a class=\"label label-info\" href=\'"+ service.getURI() + "\' title=\'" + service.getURI() + "\'>" + service.getURI() + "</a>"
                        + "<br/><b>Input Class: <b><a class=\"label label-info\" href=\'"+ service.getInputClassURI() + "\' title=\'" + service.getInputClassURI() + "\'>" + service.getInputClassURI() + "</a>"
                        + "<br/><b>Output Class: <b><a class=\"label label-info\" href=\'"+ service.getOutputClassURI() + "\' title=\'" + service.getOutputClassURI() + "\'>" + service.getOutputClassURI() + "</a>";
                        */
            }
            SADIServiceDescriptions += "</div>";
        } catch (Exception e) {
            logger.error(String.format("error retrieving registered services: %s", e));
            //request.setAttribute("error", e.getMessage());
        } finally {
            if (registry != null)
                registry.getModel().close();
        }

        return SADIServiceDescriptions;
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
