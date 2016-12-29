package it.polito.dp2.NFFG.sol3.client2;

import java.net.*;
import java.util.*;
import java.util.stream.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

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
	
	WebTarget getTarget() {
		return target;
	}

	@Override
	public NffgReader getNffg(String nffgName) {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}
		// TODO check now if it exists
		return new Client2NffgReader(this, nffgName);
	}

	@Override
	public Set<NffgReader> getNffgs() {
		// TODO Auto-generated method stub
		// GET /nffgs
		List<Nffg> nffgs = target.path("nffgs").request(MediaType.APPLICATION_XML).get(new GenericType<List<Nffg>>() {});

		Set<NffgReader> result = nffgs.stream().map(nffg -> new Client2NffgReader(this, nffg.getName())).collect(Collectors.toSet());
		return result;
	}

	@Override
	public Set<PolicyReader> getPolicies() {
		// TODO Auto-generated method stub
		// GET /policies
		List<Policy> policies = target.path("policies").request(MediaType.APPLICATION_XML).get(new GenericType<List<Policy>>() {});
		return policies.stream().map(p -> new Client2PolicyReader(this, p.getName())).collect(Collectors.toSet());
	}

	@Override
	public Set<PolicyReader> getPolicies(String nffgName) {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/policies
		List<Policy> policies = target.path("nffgs").path(nffgName).path("policies").request(MediaType.APPLICATION_XML).get(new GenericType<List<Policy>>() {});
		return policies.stream().map(p -> new Client2PolicyReader(this, p.getName())).collect(Collectors.toSet());
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar verificationTime) {
		// TODO Auto-generated method stub
		// GET /policies?from={verificationTime}
		return null;
	}

}
