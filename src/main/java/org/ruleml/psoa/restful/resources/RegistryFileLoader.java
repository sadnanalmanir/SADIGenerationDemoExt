package org.ruleml.psoa.restful.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jboss.resteasy.logging.Logger;
import org.sadiframework.utils.SPARQLStringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Created by sadnana on 26/03/16.
 */
public class RegistryFileLoader {

    final String REGISTRY_FILE = "sadi-registry.rdf";
    final String REGISTRY_PATH = "/tmp/sadiregistry/";

    Logger logger = Logger.getLogger(RegistryFileLoader.class);

    public RegistryFileLoader() throws MojoExecutionException {
        loadEmptySADIRegistry();
    }

    public void loadEmptySADIRegistry() throws MojoExecutionException {


        File registryPath = new File(REGISTRY_PATH).getAbsoluteFile();
        logger.info("generating service files relative to " + registryPath);
        try {
            if(registryPath.exists())
                if(registryPath.listFiles().length > 0){ // clean existing if there are files
                    FileUtils.cleanDirectory(registryPath);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This is just a note to add to the sadi-registry.rdf file when it is created form the skeleton
        String myNoteText = "The SADI Services will start from here";
        File registryFile = new File(registryPath, String.format(REGISTRY_FILE));
        try {
            writeRegistryFile(registryFile, myNoteText);
        } catch (Exception e) {
            String message = String.format("failed to write new "+ REGISTRY_FILE + " file inside %s", registryPath);
            logger.info("Error : " + message);
            throw new MojoExecutionException(message);
        }
    }

    private void writeRegistryFile(File registryFile, String myNoteText) throws Exception {
		/* write the file...
		 */
        createPath(registryFile);
        FileWriter writer = new FileWriter(registryFile);
        String template = SPARQLStringUtils.readFully(RegistryFileLoader.class.getResourceAsStream("/org/sadiframework/service/generator/templates/SADIRegistrySkeleton"));
        VelocityContext context = new VelocityContext();
        context.put("Notes", myNoteText);
        Velocity.init();
        Velocity.evaluate(context, writer, "SADI", template);
        writer.close();
        System.out.println("==========================================");
        System.out.println("=========== " + REGISTRY_FILE +" =========");
        System.out.println("==========================================");
        String emptySADIRegistryContent = org.apache.commons.io.FileUtils.readFileToString(registryFile, "UTF-8");
        if(emptySADIRegistryContent.isEmpty())
            System.out.println(REGISTRY_FILE + " is empty.");
        else
            System.out.println(emptySADIRegistryContent);

    }

    private static void createPath(File outfile) throws IOException
    {
        File parent = outfile.getParentFile();
        if (parent != null && !parent.isDirectory())
            if (!parent.mkdirs())
                throw new IOException(String.format("unable to create directory path ", parent));
    }



}
