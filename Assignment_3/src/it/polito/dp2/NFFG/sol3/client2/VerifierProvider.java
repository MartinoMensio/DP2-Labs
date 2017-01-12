package it.polito.dp2.NFFG.sol3.client2;

import java.net.*;
import java.util.*;
import java.util.function.Function;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

public class VerifierProvider implements Function<URI, NffgVerifier> {

	@Override
	public NffgVerifier apply(URI uri) {
		// TODO Auto-generated method stub

		WebTarget target = ClientBuilder.newClient().target(uri);

		List<Nffg> nffgs = target.path("nffgs").request(MediaType.APPLICATION_XML).get(new GenericType<List<Nffg>>() {
		});

		List<Policy> policies = target.path("policies").request(MediaType.APPLICATION_XML)
				.get(new GenericType<List<Policy>>() {
				});

		Client2NffgVerifier result = new Client2NffgVerifier();

		NffgTransformer nffgTransformer = new NffgTransformer();
		nffgs.stream().map(nffg -> nffgTransformer.apply(nffg)).forEach(nffgR -> {
			try {
				result.addNffg(nffgR);
			} catch (NffgVerifierException e) {
				throw new RuntimeException(e);
			}
		});

		policies.stream().forEach(policy -> {
			Client2NffgReader nffgR = (Client2NffgReader) result.getNffg(policy.getNffg());
			if (nffgR == null) {
				throw new RuntimeException("Nffg name does not match on policy " + policy.getName());
			}
			try {
				result.addPolicy(nffgR.getName(), new PolicyTransformer(nffgR).apply(policy));
			} catch (NffgVerifierException e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

}
