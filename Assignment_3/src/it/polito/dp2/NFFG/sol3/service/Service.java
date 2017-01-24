package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.stream.*;

import javax.ws.rs.*;
import javax.xml.datatype.*;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;
import it.polito.dp2.NFFG.sol3.service.neo4j.Neo4JXMLClient;

/**
 * Core functionality: orchestrator + response builder. Called by the web
 * interface, this class interacts with the persistence and the Neo4JXML client
 * 
 * @author Martino Mensio
 *
 */
public class Service {

	private ObjectFactory factory;

	private Neo4JXMLClient neoClient;

	private DataStorage data;

	/**
	 * This lock allows two kinds of locking and is only used to protect
	 * consistency against the deletion of NFFGs from the service. Since the
	 * implementation of the deletion is not required, also this locking is not
	 * required if no one uses the deletion. It is used in the following way:
	 * <ul>
	 * <li>as shared lock: by all the methods that manipulate policies and
	 * therefore need that the corresponding NFFG is not removed while they are
	 * performing some actions on the stored data</li>
	 * <li>as exclusive lock: by the remove NFFG method, in order to make
	 * atomically the deletion of the nffg with its policies</li>
	 * </ul>
	 * The usage of shared lock was chosen in order to allow multiple thread
	 * that own it to go into the sections of code.
	 */
	private StampedLock l;

	private Service(URI neo4jLocation, DataStorage data) {
		factory = new ObjectFactory();
		neoClient = new Neo4JXMLClient(neo4jLocation);
		this.data = data;
		l = new StampedLock();
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
		return data.getNffgsMap().values().stream().map(NffgStorage::getNffg).collect(Collectors.toList());
	}

	public Nffg getNffg(String name) {
		NffgStorage nffgStorage = data.getNffgsMap().get(name);
		return (nffgStorage != null) ? nffgStorage.getNffg() : null;
	}

	public Nffg storeNffg(Nffg nffg) {
		// check anyway the nodes referred by the links, just in the case XML
		// validation is disabled
		Set<String> refs = Stream.concat(nffg.getLink().stream().map(l -> l.getSrc().getRef()),
				nffg.getLink().stream().map(l -> l.getDst().getRef())).collect(Collectors.toSet());
		if (!nffg.getNode().stream().map(Node::getName).collect(Collectors.toSet()).containsAll(refs)) {
			throw new BadRequestException("the nffg contains reference to non existing nodes in some links");
		}

		ConcurrentMap<String, String> idMappings = new ConcurrentHashMap<>();

		// set updateTime before storing the nffg
		GregorianCalendar now = new GregorianCalendar();
		try {
			nffg.setUpdated(DatatypeFactory.newInstance().newXMLGregorianCalendar(now));
		} catch (DatatypeConfigurationException e) {
			// this never happens, but let's be fail-safe
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
		// acquiring the lock is not required because the concurrency on the map
		// of nffgs is already managed by the ConcurrentMap, and the
		// synchronization will take place when creating new policies and asking
		// for a shared lock
		if (data.getNffgsMap().putIfAbsent(nffg.getName(), nffgStorage) != null) {
			return null;
		}

		try {
			// Add the nffg fake node to neo4j
			String nffgId = neoClient.addNamedNode(nffg.getName());
			neoClient.addNffgLabelToNode(nffgId);

			// adding all the nodes to neo4j in parallel
			nffg.getNode().parallelStream().forEach(node -> {
				String nodeId = neoClient.addNamedNode(node.getName());
				// store the ID of the node
				idMappings.put(node.getName(), nodeId);
				// add the belongs relationship
				neoClient.addBelongsToNffg(nffgId, nodeId);
			});

			// and add the links to neo4j in parallel
			nffg.getLink().parallelStream().forEach(link -> {
				String srcNodeId = idMappings.get(link.getSrc().getRef());
				if (srcNodeId == null) {
					throw new InconsistentDataException(
							"error caused by srcNodeId not found for node " + link.getSrc().getRef());
				}
				String dstNodeId = idMappings.get(link.getDst().getRef());
				if (dstNodeId == null) {
					throw new InconsistentDataException(
							"error caused by dstNodeId not found for node " + link.getDst().getRef());
				}
				neoClient.addLinkBetweenNodes(srcNodeId, dstNodeId);
			});
		} finally {
			// something went wrong
			nffgStorage.setKO();
		}
		// now that the id map is filled, can unlock the readers of it
		nffgStorage.setOK();

		return nffg;
	}

	/**
	 * This is the only method that requires additional synchronization, because
	 * invalidates the references done in policies.
	 * 
	 * @param nffgName
	 * @return
	 */
	public Nffg deleteNffg(String nffgName, boolean force) {
		NffgStorage nffgStorage = null;
		// acquire exclusive lock to block insertion or verification of policies
		// that may refer this nffg
		long stamp = l.writeLock();
		try {
			if (data.getPoliciesMap().values().stream().filter(n -> n.getNffg().equals(nffgName)).count() > 0) {
				if (force == false) {
					throw new ForbiddenException(
							"Some policies are linked to this nffg. To remove the nffg append ?force=true on the path");
				}
				// delete all the policies belonging to this nffg
				data.getPoliciesMap().entrySet().removeIf(e -> e.getValue().getNffg().equals(nffgName));
			}
			nffgStorage = data.getNffgsMap().remove(nffgName);
		} finally {
			l.unlockWrite(stamp);
		}
		return (nffgStorage != null) ? nffgStorage.getNffg() : null;
	}

	public Policy verifyResultOnTheFly(Policy policy) {
		// acquire shared lock
		long stamp = l.readLock();
		try {
			validateReferences(policy);
			// the data referenced don't change because nffg cannot be deleted
			// here, thanks to locking
			return verifyPolicy(policy);
		} finally {
			l.unlockRead(stamp);
		}
	}

	/**
	 * 
	 * @param policy
	 * @return the old value
	 */
	public Policy storePolicy(Policy policy) {
		// acquire shared lock
		long stamp = l.readLock();
		try {
			validateReferences(policy);
			// the data referenced don't change because nffg cannot be deleted
			// here, thanks to locking
			return data.getPoliciesMap().put(policy.getName(), policy);
		} finally {
			l.unlockRead(stamp);
		}
	}

	public Policy deletePolicy(String policyName) {
		// acquire shared lock
		long stamp = l.readLock();
		try {
			return data.getPoliciesMap().remove(policyName);
		} finally {
			l.unlockRead(stamp);
		}
	}

	public List<Policy> getPolicies(String nffgName) {
		long stamp = l.readLock();
		try {
			return data.getPoliciesMap().values().stream().filter(p -> {
				if (nffgName == null) {
					// no filtering
					return true;
				}
				return p.getNffg().equals(nffgName);
			}).collect(Collectors.toList());
		} finally {
			l.unlockRead(stamp);
		}
	}

	public Policy getPolicy(String policyName) {
		return data.getPoliciesMap().get(policyName);
	}

	public Policy updatePolicyResult(String policyName) {
		long stamp = l.readLock();
		try {
			Policy policy = getPolicy(policyName);
			if (policy == null) {
				// caller will call NotFoundException
				return null;
			}
			// the data referenced don't change because nffg cannot be deleted
			// here, thanks to locking
			return verifyPolicy(policy);
		} finally {
			l.unlockRead(stamp);
		}
	}

	/**
	 * Called by other methods above. This is a slave method. Considering
	 * synchronization issues with removeNffg, this method is always called by a
	 * block protected by sharedLock, in order to be sure that the nffg is
	 * always existing (the policy could have been removed / never been stored
	 * on the service)
	 * 
	 * @param policy
	 * @return
	 */
	public Policy verifyPolicy(Policy policy) {

		NffgStorage nffgStorage = data.getNffgsMap().get(policy.getNffg());
		if (nffgStorage == null) {
			throw new InconsistentDataException("in the verification process the nffg was not found");
		}
		String srcId = nffgStorage.getId(policy.getSrc().getRef());
		String dstId = nffgStorage.getId(policy.getDst().getRef());
		if (srcId == null || dstId == null) {
			throw new InconsistentDataException(
					"in the verification process there was an error retrieving the neo4j nodes id");
		}
		boolean reachabilityStatus = neoClient.testReachability(srcId, dstId);
		Result result = factory.createResult();
		boolean satisfied = (reachabilityStatus == policy.isPositive());
		result.setSatisfied(satisfied);
		result.setContent("the policy is " + (satisfied ? "" : "not ") + "satisfied: expectation=" + policy.isPositive()
				+ " actual=" + reachabilityStatus);
		GregorianCalendar now = new GregorianCalendar();
		try {
			result.setVerified(DatatypeFactory.newInstance().newXMLGregorianCalendar(now));
		} catch (DatatypeConfigurationException e) {
			// this never happens, but let's be fail-safe
			result.setVerified(null);
		}
		policy.setResult(result);
		return policy;
	}

	/**
	 * Checks that the referred nffg exists and contains src and dst nodes
	 * 
	 * @param policy
	 */
	public void validateReferences(Policy policy) {
		NffgStorage nffgStorage = data.getNffgsMap().get(policy.getNffg());
		if (nffgStorage == null) {
			throw new MissingReferenceException("the policy refers to inexistent nffg named " + policy.getNffg());
		}
		if (nffgStorage.getId(policy.getSrc().getRef()) == null) {
			throw new MissingReferenceException("the policy source node named " + policy.getSrc().getRef()
					+ " does not belong to stored nffg named " + policy.getNffg());
		}
		if (nffgStorage.getId(policy.getDst().getRef()) == null) {
			throw new MissingReferenceException("the policy destination node named " + policy.getDst().getRef()
					+ " does not belong to stored nffg named " + policy.getNffg());
		}
	}
}
