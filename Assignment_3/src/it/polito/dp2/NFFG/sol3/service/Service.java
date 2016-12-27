package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;
import java.util.stream.*;

import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;
import it.polito.dp2.NFFG.sol3.service.wjc.*;

/**
 * Core functionality: orchestrator + response builder. Called by the web
 * interface, this class interacts with the persistence and the Neo4JXML client
 * 
 * @author Martino Mensio
 *
 */
public class Service {

	private Neo4JXMLClient neoClient;

	// retrieve data from persistence
	private Persistence data;

	private Service(URI neo4jLocation) {
		neoClient = new Neo4JXMLClient(neo4jLocation);
		data = Persistence.getPersistence();
	}

	public static Service createService() throws ServiceException {
		String url = System.getProperty("it.polito.dp2.NFFG.lab3.NEO4JURL");
		if (url == null) {
			url = "http://localhost:8080/Neo4JXML/rest";
		}
		try {
			return new Service(URI.create(url));
		} catch (IllegalArgumentException e) {
			throw new ServiceException(e);
		}
	}

	public List<NffgT> getNffgs() {
		return data.nffgsMap.values().stream().collect(Collectors.toList());
	}

	public NffgT getNffg(String name) {
		return data.nffgsMap.get(name);
	}
	
	public NffgT storeNffg(NffgT nffg) {
		
		// check the uniqueness of the nffg name
		if(data.nffgsMap.containsKey(nffg.getName())) {
			return null;
		}
		
		// Add the nffg fake node to neo4j
		Node nffgN = addNamedNode(nffg.getName());
		neoClient.addNffgLabelToNode(nffgN.getId());
		
		// adding all the nodes to neo4j
		for(NodeT node : nffg.getNode()) {
			Node nodeN = addNamedNode(node.getName());
			neoClient.addBelongsToNffg(nffgN.getId(), nodeN.getId());
		}
		
		// store the Nffg in the persistence
		data.nffgsMap.put(nffg.getName(), nffg);
		return nffg;
	}
	
	public PolicyT storePolicy(PolicyT policy) {
		// TODO check all the constraints
		data.policiesMap.put(policy.getName(), policy);
		return policy;
	}
	
	Node addNamedNode(String nodeName) {
		Node nodeReq = new Node();
		Property nameProp = new Property();
		nameProp.setName("name");
		nameProp.setValue(nodeName);
		nodeReq.getProperty().add(nameProp);
		// TODO exceptions
		Node nodeRes = neoClient.addNode(nodeReq);
		return nodeRes;
	}
}
