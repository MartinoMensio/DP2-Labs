package it.polito.dp2.NFFG.sol3.client2;

import java.util.*;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2NFFGVerifier implements NffgVerifier {

	@Override
	public NffgReader getNffg(String nffgName) {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}
		return null;
	}

	@Override
	public Set<NffgReader> getNffgs() {
		// TODO Auto-generated method stub
		// GET /nffgs
		return null;
	}

	@Override
	public Set<PolicyReader> getPolicies() {
		// TODO Auto-generated method stub
		// GET /policies
		return null;
	}

	@Override
	public Set<PolicyReader> getPolicies(String policyName) {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}
		return null;
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar verificationTime) {
		// TODO Auto-generated method stub
		// GET /policies?from={verificationTime}
		return null;
	}

}
