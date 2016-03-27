package org.ruleml.psoa.restful.resources;

/**
 * Created by sadnana on 06/03/16.
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


//@Path("/registry")
public class SADICodeViewer {
/*
    boolean doValidate;
    boolean doRegister;
    boolean doTweet;

    public SADICodeViewer(){
        doValidate = false;
        doRegister = true;
        doTweet = false;

    }
    org.jboss.resteasy.logging.Logger logger = org.jboss.resteasy.logging.Logger.getLogger(SADICodeViewer.class);
    @Context
    UriInfo info;

    @Path("/register")
    @POST
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded

    public String completeRegistration(){

        String serviceURI = "http://cbakerlab.unbsj.ca:8080/getDiagnosisCodeByDiagnosisID_demo/getDiagnosisCodeByDiagnosisID";
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
                    // TODO replace with validateService(service) once we update the API...
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
        try {
            registry = Registry.getRegistry();
            //pageContext.setAttribute("services", registry.getRegisteredServices());
            Collection<ServiceBean> services = registry.getRegisteredServices();
            for (ServiceBean service : services) {
                System.out.println(service.getURI());
                System.out.println(service.getInputClassURI());
                System.out.println(service.getOutputClassURI());
            }
        } catch (Exception e) {
            logger.error(String.format("error retrieving registered services: %s", e));
            //request.setAttribute("error", e.getMessage());
        } finally {
            if (registry != null)
                registry.getModel().close();
        }

        return "services displayed";
    }



*/
}
