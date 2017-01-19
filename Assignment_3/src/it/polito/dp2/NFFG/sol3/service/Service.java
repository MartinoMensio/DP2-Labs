package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;
import java.util.stream.*;

import javax.xml.datatype.*;

import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.service.exceptions.*;
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
	private DataStorage data;

	private Service(URI neo4jLocation, DataStorage data) {
		neoClient = new Neo4JXMLClient(neo4jLocation);
		this.data = data;
	}
	
	public final static Service standardService = createStandardService();

	private static Service createStandardService() {
		String url = System.getProperty("it.polito.dp2.NFFG.lab3.NEO4JURL");
		if (url == null) {
			url = "http://localhost:8080/Neo4JXML/rest";
		}
		try {
			return new Service(URI.create(url), DataStorage.getData());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public List<Nffg> getNffgs() {
		return data.nffgsMap.values().stream().map(NffgStorage::getNffg).collect(Collectors.toList());
	}

	public Nffg getNffg(String name) {
		NffgStorage nffgStorage = data.nffgsMap.get(name);
		return (nffgStorage != null)? nffgStorage.getNffg() : null;
	}

	public Nffg storeNffg(Nffg nffg) {
		Map<String, String> idMappings = new HashMap<>();

		// set updateTime before storing the nffg
		GregorianCalendar now = new GregorianCalendar();
		try {
			nffg.setUpdated(DatatypeFactory.newInstance().newXMLGregorianCalendar(now));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			nffg.setUpdated(null);
		}

		// Store the Nffg in the persistence only if no other nffg stored has
		// the same name. The check on uniqueness needs to be done as first
		// operation, in order to avoid useless data to be stored inside the
		// neo4j service (useless because if the nffg won't be stored it is only
		// a loss of time to store all its nodes inside the neo4j service and
		// then trying to rollback when doing the check.
		// If the check is done at the beginning but the storage inside the map
		// of nffgs is done at the end, the whole method is prone to
		// synchronization issues and the whole method needs to synchronize in
		// order to avoid interleaving due to non-atomic design. This would also
		// lead to performance problems (because network operations are the
		// slowest). This issue is solved by calling a single operation on the
		// map of nffgs that tests and insert atomically on the map. After this
		// instruction, there is no need of synchronization in order to
		// communicate with the neo4j service (can be performed in parallel).
		//
		// The only required synchronization that this way of acting has as a
		// consequence, is that the map of ids is not yet filled when a nffg is
		// stored. So other threads could try to read it in order to evaluate
		// reachability. For this reason a synchronization scheme has been added
		// to the NffgStorage to avoid this problem:
		// - when the NffgStorage is created, the map of ids is in a locked
		// state
		// - threads reading the mappings are blocked until the map is not ready
		// - the storeNffg method calls a method on the NffgStorage to unlock
		// the map when the data is ready (and will never be modified again
		// because the reference to the map is lost).
		//
		NffgStorage nffgStorage = new NffgStorage(nffg, idMappings);
		if (data.nffgsMap.putIfAbsent(nffg.getName(), nffgStorage) != null) {
			return null;
		}

		try {
			// Add the nffg fake node to neo4j
			String nffgId = neoClient.addNamedNode(nffg.getName());
			neoClient.addNffgLabelToNode(nffgId);

			// adding all the nodes to neo4j
			for (Node node : nffg.getNode()) {
				String nodeId = neoClient.addNamedNode(node.getName());
				// store the ID of the node
				idMappings.put(node.getName(), nodeId);
				// add the belongs relationship
				neoClient.addBelongsToNffg(nffgId, nodeId);
			}

			// and add the links to neo4j
			for (Link link : nffg.getLink()) {
				String srcNodeId = idMappings.get(link.getSrc().getRef());
				String dstNodeId = idMappings.get(link.getDst().getRef());
				if (srcNodeId == null || dstNodeId == null) {
					throw new Exception();
				}
				neoClient.addLinkBetweenNodes(srcNodeId, dstNodeId);
			}
		} catch (Exception e) {
			// something went wrong
			nffgStorage.setKO();
			return null;
		}
		// now that the id map is filled, can unlock the readers of it
		nffgStorage.setOK();

		return nffg;
	}

	public Nffg deleteNffg(String nffgName) {
		NffgStorage nffgStorage = data.nffgsMap.remove(nffgName);
		return (nffgStorage != null) ? nffgStorage.getNffg() : null;
	}

	public Policy verifyResultOnTheFly(Policy policy) {
		validateReferences(policy);
		Policy verified = verifyPolicy(policy);
		return verified;
	}

	public Policy storePolicy(Policy policy) {
		validateReferences(policy);
		data.policiesMap.put(policy.getName(), policy);
		return policy;
	}

	public Policy deletePolicy(String policyName) {
		return data.policiesMap.remove(policyName);
	}

	public List<Policy> getPolicies() {
		return data.policiesMap.values().stream().collect(Collectors.toList());
	}

	public List<Policy> getNffgPolicies(String nffgName) {
		if (data.nffgsMap.get(nffgName) == null) {
			return null;
		}
		return data.policiesMap.values().stream().filter(p -> p.getNffg().equals(nffgName))
				.collect(Collectors.toList());
	}

	public Policy verifyPolicy(Policy policy) {
		
		NffgStorage nffgStorage = data.nffgsMap.get(policy.getNffg());
		if (nffgStorage == null) {
			return null;
		}
		String srcId = nffgStorage.getId(policy.getSrc().getRef());
		String dstId = nffgStorage.getId(policy.getDst().getRef());
		if (srcId == null || dstId == null) {
			// TODO
			return null;
		}
		boolean reachabilityStatus = neoClient.testReachability(srcId, dstId);
		Result result = new Result();
		boolean satisfied = reachabilityStatus == policy.isPositive();
		result.setSatisfied(satisfied);
		result.setContent("the policy is " + (satisfied ? "" : "not ") + "satisfied: expectation=" + policy.isPositive()
				+ " actual=" + reachabilityStatus);
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
		return data.policiesMap.get(policyName);
	}

	public Policy updatePolicyResult(String policyName) {
		Policy policy = getPolicy(policyName);
		if (policy == null) {
			return null;
		}
		return verifyPolicy(policy);
	}
	
	/**
	 * Checks that the referred nffg exists and contains src and dst nodes
	 * @param policy
	 */
	public void validateReferences(Policy policy) {
		NffgStorage nffgStorage = data.nffgsMap.get(policy.getNffg());
		if (nffgStorage == null) {
			throw new ValidationFailedException("the policy refers to inexistent nffg named " + policy.getNffg());
		}
		if (nffgStorage.getId(policy.getSrc().getRef()) == null) {
			throw new ValidationFailedException("the policy source node named " + policy.getSrc().getRef() + " does not belong to stored nffg named " + policy.getNffg());
		}
		if (nffgStorage.getId(policy.getDst().getRef()) == null) {
			throw new ValidationFailedException("the policy destination node named " + policy.getDst().getRef() + " does not belong to stored nffg named " + policy.getNffg());
		}
	}
}
