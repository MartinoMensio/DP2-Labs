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
public class Client2PolicyReader extends Client2NamedEntityReader implements PolicyReader {

	private WebTarget target;
	
	Client2PolicyReader(Client2NffgVerifier verifier, String name) {
		super(name);
		target = verifier.getTarget();
	}

	@Override
	public NffgReader getNffg() {
		// TODO Auto-generated method stub
		// GET /policies/{policyId}/nffg
		return null;
	}

	@Override
	public VerificationResultReader getResult() {
		// TODO Auto-generated method stub
		// GET /policies/{policyId}/result
		ResultT result = target.path("policies").path(getName()).path("result").request(MediaType.APPLICATION_JSON).get(ResultT.class);
		return new Client2VerificationResultReader(getName(), result.isSatisfied(), result.getContent(), result.getVerified());
	}

	@Override
	public Boolean isPositive() {
		// TODO Auto-generated method stub
		// GET /policies/{policyId} to get fresh data
		// return policy.isPositive
		return null;
	}

}
