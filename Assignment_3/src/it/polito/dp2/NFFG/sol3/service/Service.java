package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;
import java.util.stream.*;

import javax.xml.datatype.*;

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

	public List<Nffg> getNffgs() {
		return data.nffgsMap.values().stream().collect(Collectors.toList());
	}

	public Nffg getNffg(String name) {
		return data.nffgsMap.get(name);
	}
	
	public Nffg storeNffg(Nffg nffg) {
		
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
			// store the ID of the node
			data.nodesId.put(node.getName(), nodeN.getId());
			// add the belongs relationship
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
	
	public PolicyT deletePolicy(String policyName) {
		return data.policiesMap.remove(policyName);
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

	public List<PolicyT> getPolicies() {
		// TODO Auto-generated method stub
		return data.policiesMap.values().stream().collect(Collectors.toList());
	}
	
	public List<PolicyT> getNffgPolicies(String nffgName) {
		// TODO Auto-generated method stub
		if(data.nffgsMap.get(nffgName) == null) {
			return null;
		}
		return data.policiesMap.values().stream().filter(p -> p.getNffg().equals(nffgName)).collect(Collectors.toList());
	}

	public PolicyT verifyPolicy(PolicyT policy) {
		// TODO Auto-generated method stub
		String srcId = data.nodesId.get(policy.getSrc().getRef());
		String dstId = data.nodesId.get(policy.getDst().getRef());
		if(srcId == null || dstId == null) {
			// TODO
			return null;
		}
		boolean reachabilityStatus = neoClient.testReachability(srcId, dstId);
		ResultT result = new ResultT();
		boolean satisfied = reachabilityStatus == policy.isPositive();
		result.setSatisfied(satisfied);
		result.setContent("the policy is " + (satisfied? "" : "not ") + "satisfied: expectation=" + policy.isPositive() + " actual=" + reachabilityStatus);
		GregorianCalendar now = new GregorianCalendar();
		try {
			result.setVerified(DatatypeFactory.newInstance().newXMLGregorianCalendar(now));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setVerified(null);
		}
		policy.setResult(result);
		return policy;
	}

	public PolicyT getPolicy(String policyName) {
		// TODO Auto-generated method stub
		return data.policiesMap.get(policyName);
		// TODO verify again?
	}

}
