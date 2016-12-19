package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2PolicyReader extends Client2NamedEntityReader implements PolicyReader {

	Client2PolicyReader(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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
		return null;
	}

	@Override
	public Boolean isPositive() {
		// TODO Auto-generated method stub
		// GET /policies/{policyId} to get fresh data
		// return policy.isPositive
		return null;
	}

}
