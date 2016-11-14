package it.polito.dp2.NFFG.sol1;

import java.util.*;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;

public class MyNffgVerifier implements NffgVerifier {
	
	private Map<String,NffgReader> nffgs;
	// for each nffg, the corresponding set of policies
	private Map<String,Set<PolicyReader>> policies;

	public MyNffgVerifier() {
		this.nffgs = new HashMap<>();
		this.policies = new HashMap<>();
	}
	
	void addNffg(NffgReader nffg) {
		// TODO check return values
		nffgs.put(nffg.getName(), nffg);
		policies.put(nffg.getName(), new HashSet<>());
	}
	
	void addPolicy(String nffgName, PolicyReader policy) {
		// TODO check return value
		policies.get(nffgName).add(policy);
	}

	@Override
	public NffgReader getNffg(String nffgName) {
		return nffgs.get(nffgName);
	}

	@Override
	public Set<NffgReader> getNffgs() {
		return new HashSet<>(nffgs.values());
	}

	@Override
	public Set<PolicyReader> getPolicies() {
		Set<PolicyReader> result = policies.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
		return (result.size() > 0) ? result : null;
	}

	@Override
	public Set<PolicyReader> getPolicies(String nffgName) {
		return policies.get(nffgName);
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar verificationTime) {
		Set<PolicyReader> result = policies.values().stream().flatMap(Set::stream).filter(p -> {
			return p.getResult().getVerificationTime().compareTo(verificationTime) > 0;
		}).collect(Collectors.toSet());
		return (result.size() > 0) ? result : null;
	}

}
