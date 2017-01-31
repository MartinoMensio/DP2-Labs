package it.polito.dp2.NFFG.sol3.client1;

import java.net.*;
import java.util.*;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.lab3.test0.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class NFFGClient0Impl extends NFFGClientImpl implements NFFGClient0 {

	public NFFGClient0Impl(NffgVerifier verifier, URI uri) {
		super(verifier, uri);
	}

	@Override
	public void loadIsolationPolicy(String name, String nffgName, boolean isPositive, Set<String> nodeSet1,
			Set<String> nodeSet2) throws UnknownNameException, ServiceException {
		// check the references
		NffgReader nffgR = verifier.getNffg(nffgName);
		if (nffgR == null) {
			throw new UnknownNameException("the policy refers to a locally unknown nffg " + nffgName);
		}

		List<String> nodeNames = nffgR.getNodes().stream().map(NodeReader::getName).collect(Collectors.toList());
		if (!nodeNames.containsAll(nodeSet1)) {
			throw new UnknownNameException("the policy refers an unknown node in the first set");
		}
		if (!nodeNames.containsAll(nodeSet2)) {
			throw new UnknownNameException("the policy refers an unknown node in the second set");
		}

		List<NodeReader> firstSet = nffgR.getNodes().stream().filter(n -> nodeSet1.contains(n.getName()))
				.collect(Collectors.toList());
		List<NodeReader> secondSet = nffgR.getNodes().stream().filter(n -> nodeSet2.contains(n.getName()))
				.collect(Collectors.toList());

		// build a Policy from the values
		ObjectFactory factory = policyReaderTransformer.getFactory();
		Policy policy = factory.createPolicy();
		policy.setName(name);
		PolicySpecification spec = factory.createPolicySpecification();
		Isolation isolation = factory.createIsolation();
		firstSet.stream().forEach(n -> {
			NodeRef ref = factory.createNodeRef();
			ref.setRef(n.getName());
			isolation.getFirstSet().add(ref);
		});
		secondSet.stream().forEach(n -> {
			NodeRef ref = factory.createNodeRef();
			ref.setRef(n.getName());
			isolation.getSecondSet().add(ref);
		});

		spec.setIsolation(isolation);
		policy.setSpecification(spec);
		policy.setNffg(nffgName);
		policy.setPositive(isPositive);
		// load the policy
		loadPolicy(policy);

	}

	@Override
	public void unloadIsolationPolicy(String name) throws UnknownNameException, ServiceException {
		super.unloadReachabilityPolicy(name);
	}

}
