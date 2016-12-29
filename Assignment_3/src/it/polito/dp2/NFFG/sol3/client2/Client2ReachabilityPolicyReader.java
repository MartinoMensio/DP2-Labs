package it.polito.dp2.NFFG.sol3.client2;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2ReachabilityPolicyReader extends Client2PolicyReader implements ReachabilityPolicyReader {

	private Client2NffgReader nffgReader;
	protected WebTarget target;
	
	Client2ReachabilityPolicyReader(Client2NffgVerifier verifier, String name) {
		super(verifier, name);
		nffgReader = new Client2NffgReader(verifier, getNffg().getName());
		target = verifier.getTarget();
	}

	@Override
	public NodeReader getDestinationNode() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}/dst
		// TODO implement this resource on server
		NodeT node = target.path("policies").path(getName()).path("dst").request(MediaType.APPLICATION_JSON).get(NodeT.class);
		return new Client2NodeReader(nffgReader, node.getName());
	}

	@Override
	public NodeReader getSourceNode() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}/src
		// TODO implement this resource on server
		NodeT node = target.path("policies").path(getName()).path("src").request(MediaType.APPLICATION_JSON).get(NodeT.class);
		return new Client2NodeReader(nffgReader, node.getName());
	}

}
