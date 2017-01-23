package it.polito.dp2.NFFG.sol3.client2;

import java.net.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

public class NffgVerifierDataProvider {
	
	private WebTarget target;
	
	public NffgVerifierDataProvider(URI uri) {
		target = ClientBuilder.newClient().target(uri);
	}

	public NffgVerifier getNffgVerifierData() throws NffgVerifierException {
		// read the nffgs
		List<Nffg> nffgs = downloadNffgs();
		// read the policies
		List<Policy> policies = downloadPolicies();

		Client2NffgVerifier result = new Client2NffgVerifier();

		// work on the nffgs
		NffgTransformer nffgTransformer = new NffgTransformer();
		for(Nffg nffg : nffgs) {
			// transform them to NffgReader
			NffgReader nffgR = nffgTransformer.apply(nffg);
			// and add them to the NffgVerifier
			result.addNffg(nffgR);
		}

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

	List<Nffg> downloadNffgs() throws NffgVerifierException {
		Response res;
		try {
			res = target.path("nffgs").request(MediaType.APPLICATION_XML).get();
			if (res.getStatus() != 200) {
				throw new NffgVerifierException(
						"Download of NFFGs failed. Response status code was " + res.getStatus() + " instead of 200");
			}
			return res.readEntity(new GenericType<List<Nffg>>() {
			});
		} catch (ProcessingException e) {
			throw new NffgVerifierException("Download of NFFGs failed. Reason: " + e.getMessage());
		}
	}

	List<Policy> downloadPolicies() throws NffgVerifierException {
		Response res;
		try {
			res = target.path("policies").request(MediaType.APPLICATION_XML).get();
			if (res.getStatus() != 200) {
				throw new NffgVerifierException(
						"Download of policies failed. Response status code was " + res.getStatus() + " instead of 200");
			}
			return res.readEntity(new GenericType<List<Policy>>() {
			});
		} catch (ProcessingException e) {
			throw new NffgVerifierException("Download of policies failed. Reason: " + e.getMessage());
		}
	}
}
