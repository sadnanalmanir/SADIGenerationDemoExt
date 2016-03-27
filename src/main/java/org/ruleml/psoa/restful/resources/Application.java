package org.ruleml.psoa.restful.resources;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jboss.resteasy.logging.Logger;



public class Application extends javax.ws.rs.core.Application {

    Logger logger = Logger.getLogger(Application.class);
    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();

    public Application() throws MojoExecutionException {
        // ADD YOUR RESTFUL RESOURCES HERE
        logger.info("inside Application class....");
        this.singletons.add(new DomainOntologyLoader());
        this.singletons.add(new ServiceOntologyLoader());
        try {
            this.singletons.add(new SADICodeGeneratorRunner());
        } catch (MojoFailureException e) {
            e.printStackTrace();
        } catch (MojoExecutionException e) {
            e.printStackTrace();
        }

        this.singletons.add(new RegistryFileLoader());

        this.singletons.add(new ServiceRegistrationLoader());

        //this.singletons.add(new SADICodeViewer());
        this.singletons.add(new SADIServiceRegistration());

    }

    @Override
    public Set<Class<?>> getClasses() {
        return this.empty;
    }

    @Override
    public Set<Object> getSingletons() {
        return this.singletons;
    }
}
