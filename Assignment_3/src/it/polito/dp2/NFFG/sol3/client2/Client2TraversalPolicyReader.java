package it.polito.dp2.NFFG.sol3.client2;

import java.util.*;
import java.util.stream.*;

import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2TraversalPolicyReader extends Client2ReachabilityPolicyReader implements TraversalPolicyReader {

	Client2TraversalPolicyReader(Client2NffgVerifier verifier, String name) {
		super(verifier, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<FunctionalType> getTraversedFuctionalTypes() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName} to have fresh data
		// return policy.functionalTypes
		Policy policy = target.path("policies").path(getName()).request(MediaType.APPLICATION_XML).get(Policy.class);
		return policy.getFunctionality().stream().map(f -> FunctionalType.fromValue(f.value())).collect(Collectors.toSet());
	}

}
