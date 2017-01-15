package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;
import java.util.stream.*;

import javax.xml.datatype.*;

import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;
import it.polito.dp2.NFFG.sol3.service.wjc.Neo4JXMLClient;;

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
		return data.nffgsMap.values().stream().map(NffgStorage::getNffg).collect(Collectors.toList());
	}

	public Nffg getNffg(String name) {
		return data.nffgsMap.get(name).getNffg();
	}
	
	public Nffg storeNffg(Nffg nffg) {
		
		// check the uniqueness of the nffg name
		if(data.nffgsMap.containsKey(nffg.getName())) {
			return null;
		}
		
		// Add the nffg fake node to neo4j
		String nffgId = neoClient.addNamedNode(nffg.getName());
		neoClient.addNffgLabelToNode(nffgId);
		
		Map<String, String> idMappings = new HashMap<>();
		
		// adding all the nodes to neo4j
		for(Node node : nffg.getNode()) {
			String nodeId = neoClient.addNamedNode(node.getName());
			// store the ID of the node
			idMappings.put(node.getName(), nodeId);
			// add the belongs relationship
			neoClient.addBelongsToNffg(nffgId, nodeId);
		}
		
		// and add the links to neo4j
		for(Link link : nffg.getLink()) {
			String srcNodeId = idMappings.get(link.getSrc().getRef());
			String dstNodeId = idMappings.get(link.getDst().getRef());
			if (srcNodeId == null || dstNodeId == null) {
				return null;
			}
			neoClient.addLinkBetweenNodes(srcNodeId, dstNodeId);
		}
		
		// set updateTime
		GregorianCalendar now = new GregorianCalendar();
		try {
			nffg.setUpdated(DatatypeFactory.newInstance().newXMLGregorianCalendar(now));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			nffg.setUpdated(null);
		}
		
		// store the Nffg in the persistence
		data.nffgsMap.put(nffg.getName(), new NffgStorage(nffg, idMappings));
		return nffg;
	}
	
	public Nffg deleteNffg(String nffgName) {
		return data.nffgsMap.remove(nffgName).getNffg();
	}
	
	public Result verifyResultOnTheFly(Policy policy, String nffgName) {
		Policy verified = verifyPolicy(policy);
		return verified.getResult();
	}
	
	public Policy storePolicy(Policy policy) {
		// TODO check all the constraints
		data.policiesMap.put(policy.getName(), policy);
		return policy;
	}
	
	public Policy deletePolicy(String policyName) {
		return data.policiesMap.remove(policyName);
	}

	public List<Policy> getPolicies() {
		// TODO Auto-generated method stub
		return data.policiesMap.values().stream().collect(Collectors.toList());
	}
	
	public List<Policy> getNffgPolicies(String nffgName) {
		// TODO Auto-generated method stub
		if(data.nffgsMap.get(nffgName) == null) {
			return null;
		}
		return data.policiesMap.values().stream().filter(p -> p.getNffg().equals(nffgName)).collect(Collectors.toList());
	}

	public Policy verifyPolicy(Policy policy) {
		// TODO Auto-generated method stub
		String srcId = data.nffgsMap.get(policy.getNffg()).getId(policy.getSrc().getRef());
		String dstId = data.nffgsMap.get(policy.getNffg()).getId(policy.getDst().getRef());
		if(srcId == null || dstId == null) {
			// TODO
			return null;
		}
		boolean reachabilityStatus = neoClient.testReachability(srcId, dstId);
		Result result = new Result();
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

	public Policy getPolicy(String policyName) {
		// TODO Auto-generated method stub
		return data.policiesMap.get(policyName);
		// TODO verify again?
	}

}
