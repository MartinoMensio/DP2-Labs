package it.polito.dp2.NFFG.sol3.client2;

import java.util.*;

import it.polito.dp2.NFFG.*;

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
		return null;
	}

}
