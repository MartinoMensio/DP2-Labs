package it.polito.dp2.NFFG.sol3.client2;

import java.net.URI;
import java.util.*;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.jaxb.ObjectFactory;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2NffgVerifier implements NffgVerifier {

	private WebTarget target;
	private ObjectFactory factory;
	
	public Client2NffgVerifier(URI uri) {
		// TODO Auto-generated constructor stub
		target = ClientBuilder.newClient().target(uri);
		factory = new ObjectFactory();
	}

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
