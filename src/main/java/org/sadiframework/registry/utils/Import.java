package org.sadiframework.registry.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import org.sadiframework.SADIException;
import org.sadiframework.registry.Registry;
import org.sadiframework.utils.QueryExecutor;
import org.sadiframework.utils.QueryExecutorFactory;

/**
 * Register all of the services listed in the old registry.
 * @author Luke McCarthy
 */
public class Import
{
	private static Logger log = Logger.getLogger(Import.class);
	
	public static void main(String[] args) throws Exception
	{
		OldRegistry oldRegistry = new OldRegistry();
		for (String serviceURI: oldRegistry.getServiceURIs()) {
			Registry newRegistry = Registry.getRegistry();
			log.info( String.format("found service %s", serviceURI) );
			try {
				newRegistry.registerService(serviceURI);
			} catch (SADIException e) {
				log.error(String.format("error registering service %s", serviceURI), e);
			} finally {
				newRegistry.getModel().close(); // so the file writes...
			}
		}
	}
	
	private static class OldRegistry
	{
		QueryExecutor backend;
		
		public OldRegistry() throws IOException
		{
			backend = QueryExecutorFactory.createSPARQLEndpointQueryExecutor("http://biordf.net/sparql", "http://sadiframework.org/registry/");
		}
		
		public Collection<String> getServiceURIs() throws SADIException
		{
			Collection<String> serviceURIs = new ArrayList<String>();
			String query = 
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX sadi: <http://sadiframework.org/ontologies/sadi.owl#> " +
				"SELECT ?service " +
				"WHERE { " +
					"?service rdf:type sadi:Service " +
				"}";
			try {
				for (Map<String, String> binding: backend.executeQuery(query)) {
					String serviceURI = binding.get("service");
					if (serviceURI != null)
						serviceURIs.add(serviceURI);
					else
						log.error( String.format("query binding had no value for service variable:\n%s", binding) );
				}
			} catch (SADIException e) {
				log.error("error querying old registry", e);
			}
			return serviceURIs;
		}
	}
}
