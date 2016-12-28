package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2ReachabilityPolicyReader extends Client2PolicyReader implements ReachabilityPolicyReader {

	Client2ReachabilityPolicyReader(Client2NffgVerifier verifier, String name) {
		super(verifier, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public NodeReader getDestinationNode() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}/dst
		return null;
	}

	@Override
	public NodeReader getSourceNode() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}/src
		return null;
	}

}
