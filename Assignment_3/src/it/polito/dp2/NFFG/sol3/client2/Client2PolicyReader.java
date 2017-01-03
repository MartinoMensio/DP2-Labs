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

	private Client2NffgVerifier verifier;
	private WebTarget target;
	
	Client2PolicyReader(Client2NffgVerifier verifier, String name) {
		super(name);
		this.verifier = verifier;
		target = verifier.getTarget();
	}
	
	Client2NffgVerifier getVerifier() {
		return verifier;
	}

	@Override
	public NffgReader getNffg() {
		// TODO Auto-generated method stub
		// GET /policies/{policyId}/nffg
		Nffg nffg = target.path("policies").path(getName()).path("nffg").request(MediaType.APPLICATION_XML).get(Nffg.class);
		return new Client2NffgReader(verifier, nffg.getName());
	}

	@Override
	public VerificationResultReader getResult() {
		// TODO Auto-generated method stub
		// GET /policies/{policyId}/result
		Result result = target.path("policies").path(getName()).path("result").request(MediaType.APPLICATION_XML).get(Result.class);
		if (result == null) {
			return null;
		}
		return new Client2VerificationResultReader(this, result.isSatisfied(), result.getContent(), result.getVerified());
	}

	@Override
	public Boolean isPositive() {
		// TODO Auto-generated method stub
		// GET /policies/{policyId} to get fresh data
		// return policy.isPositive
		Policy policy = target.path("policies").path(getName()).request(MediaType.APPLICATION_XML).get(Policy.class);
		return policy.isPositive();
	}

}
