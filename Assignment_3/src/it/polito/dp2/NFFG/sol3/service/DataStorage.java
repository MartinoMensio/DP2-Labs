package it.polito.dp2.NFFG.sol3.service;

import java.net.URI;
import java.util.concurrent.*;

import it.polito.dp2.NFFG.sol3.service.jaxb.*;
import it.polito.dp2.NFFG.sol3.service.neo4j.Neo4JXMLClient;

/**
 * Class for persistence
 * 
 * @author Martino Mensio
 *
 */
public class DataStorage {
	// singleton class
	private static DataStorage data = new DataStorage();

	// The Maps are kept private in order to avoid overwriting, getters below

	// NFFGs are cached in the service together with mappings between node name
	// and node id
	private ConcurrentMap<String, NffgStorage> nffgsMap = new ConcurrentSkipListMap<>();
	// policies are stored in the service
	private ConcurrentMap<String, Policy> policiesMap = new ConcurrentSkipListMap<>();

	private DataStorage() {
		String url = System.getProperty("it.polito.dp2.NFFG.lab3.NEO4JURL");
		if (url == null) {
			url = "http://localhost:8080/Neo4JXML/rest";
		}
		Neo4JXMLClient neoClient;
		try {
			neoClient = new Neo4JXMLClient(URI.create(url));
			neoClient.deleteAllNodes();
		} catch (IllegalArgumentException e) {
			// wrong URI
			throw new RuntimeException(e);
		} catch (Exception e) {
			// Some exceptions calling deleteAllNodes
			throw new RuntimeException(
					"impossible to delete all the nodes at persistence instantiation. Reason: " + e.getMessage());
		}

	}

	public static DataStorage getData() {
		return data;
	}

	public ConcurrentMap<String, NffgStorage> getNffgsMap() {
		return nffgsMap;
	}

	public ConcurrentMap<String, Policy> getPoliciesMap() {
		return policiesMap;
	}

}
