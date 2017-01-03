package it.polito.dp2.NFFG.sol3.service;

import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

import it.polito.dp2.NFFG.sol3.service.jaxb.*;
import it.polito.dp2.NFFG.sol3.service.wjc.*;

/**
 * Class for persistence
 * 
 * @author Martino Mensio
 *
 */
public class Persistence {
	// singleton class
	private static Persistence data = new Persistence();

	// instance data

	// NFFGs are cached in the service
	public ConcurrentMap<String, Nffg> nffgsMap = new ConcurrentSkipListMap<>();
	// policies are stored in the service
	public ConcurrentMap<String, Policy> policiesMap = new ConcurrentSkipListMap<>();
	// the service only memorizes the mappings between node name and node id
	public ConcurrentMap<String, String> nodesId = new ConcurrentSkipListMap<>();

	private Persistence() {
		// TODO erase all the data from NEO4JXML
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
			// TODO change type of exception
			throw new RuntimeException("impossible to delete all the nodes at persistence instantiation. Reason: " + e.getMessage());
		}
		 
	}

	public static Persistence getPersistence() {
		return data;
	}

}
