package org.ruleml.psoa.restful.resources;

/**
 * Created by sadnana on 23/04/16.
 */

import ca.wilkinsonlab.sadi.client.Config;
import ca.wilkinsonlab.sadi.client.Service;






public class FindServicesTest {

    public FindServicesTest(){
        findAService();
    }

    public static void findAService()
    {
        //for (Service service: Config.getConfiguration().getMasterRegistry().findServicesByPredicate("http://semanticscience.org/resource/SIO_000219") )
        for (Service service: Config.getConfiguration().getMasterRegistry().findServicesByPredicate("http://cbakerlab.unbsj.ca:8080/haitohdemo/HAI.owl#has_diagnosis_code") )
        {
            System.out.println("Finding the service...");
            System.out.println(service);
        }
    }
}
