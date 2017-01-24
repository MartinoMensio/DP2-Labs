package it.polito.dp2.NFFG.sol3.client2;

import java.net.*;
import java.util.*;
import java.util.stream.*;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.client2.library.*;
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

		NffgVerifierImpl result = new NffgVerifierImpl();

		// work on the nffgs
		ThrowingTransformer<Nffg, NffgReaderImpl, NffgVerifierException> nffgTransformer = NffgTransformer
				.newNffgTransformer();
		for (Nffg nffg : nffgs) {
			// transform them to NffgReader
			NffgReader nffgR = nffgTransformer.transform(nffg);
			// and add them to the NffgVerifier
			result.addNffg(nffgR);
		}
		// work on the policies
		Map<String, List<Policy>> policiesByNffg = policies.stream().collect(Collectors.groupingBy(Policy::getNffg));
		for (Map.Entry<String, List<Policy>> group : policiesByNffg.entrySet()) {
			NffgReaderImpl nffgR = (NffgReaderImpl) result.getNffg(group.getKey());
			if (nffgR == null) {
				throw new NffgVerifierException("No nffg with the name " + group.getKey());
			}
			ThrowingTransformer<Policy, PolicyReaderImpl, RuntimeException> policyTransformer = PolicyTransformer
					.newNffgTransformer(nffgR);
			for (Policy policy : group.getValue()) {
				result.addPolicy(nffgR.getName(), policyTransformer.transform(policy));
			}
		}
		return result;
	}

	List<Nffg> downloadNffgs() throws NffgVerifierException {
		try {
			Response res = target.path("nffgs").request(MediaType.APPLICATION_XML).get();
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
		try {
			Response res = target.path("policies").request(MediaType.APPLICATION_XML).get();
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
