package it.polito.dp2.NFFG.sol1.library;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the NffgVerifier interface
 * 
 * @author Martino Mensio
 *
 */
public class NffgVerifierImpl implements NffgVerifier {

	// the nffgs mapped by their name
	private Map<String, NffgReader> nffgs;
	// set of policies mapped by the name of the nffg they belong to
	private Map<String, Set<PolicyReader>> policies;

	public NffgVerifierImpl() {
		this.nffgs = new HashMap<>();
		this.policies = new HashMap<>();
	}

	/**
	 * adds to the private Map the specified nffg. This method does not belong
	 * to the interface
	 * 
	 * @param nffg
	 * @throws NffgVerifierException
	 *             if another nffg with this name is already there
	 */
	public void addNffg(NffgReader nffg) throws NffgVerifierException {
		if (nffgs.containsKey(nffg.getName())) {
			// a duplicate nffg is found
			throw new NffgVerifierException("A NFFG with the name " + nffg.getName() + " is already there");
		}
		// store the nffg
		nffgs.put(nffg.getName(), nffg);
		// and create a new Set for storing its policies
		policies.put(nffg.getName(), new HashSet<>());
	}

	/**
	 * Adds the policy to the verifier
	 * 
	 * @param nffgName
	 *            the name of the nffg this policy refers to
	 * @param policy
	 *            the policy to be added
	 * @throws NffgVerifierException
	 *             if there is no nffg with this name or if there is already a
	 *             policy with this name
	 */
	public void addPolicy(String nffgName, PolicyReader policy) throws NffgVerifierException {
		if (!nffgs.containsKey(nffgName)) {
			// no nffg with this name
			throw new NffgVerifierException(
					"The policy " + policy.getName() + "refers to the NFFG " + nffgName + " that does not exist");
		}
		// get a flat map of policies mapped by their name
		Map<String, PolicyReader> flatPolicies = policies.values().stream().flatMap(Set::stream)
				.collect(Collectors.toMap(PolicyReader::getName, Function.identity()));
		if (flatPolicies.containsKey(policy.getName())) {
			// duplicate policy (the scope for policy name is global
			throw new NffgVerifierException("A policy named " + policy.getName() + " already exists");
		}
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
		// get a Set of all the policies stored inside the map
		return policies.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
	}

	@Override
	public Set<PolicyReader> getPolicies(String nffgName) {
		Set<PolicyReader> result = policies.get(nffgName);
		// the result could be null if there is no nffg with this name
		// or could be an empty set if there are no policies for this nffg
		return (result != null && result.size() > 0) ? result : null;
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar verificationTime) {
		// get all the policies
		Set<PolicyReader> result = policies.values().stream().flatMap(Set::stream).filter(p -> {
			// filter lets in the stream only the policies that have been
			// verified after the VerificationTime
			return p.getResult().getVerificationTime().compareTo(verificationTime) > 0;
		}).collect(Collectors.toSet());
		// if no policy satisfies the condition, return null instead of an empty
		// Set
		return (result.size() > 0) ? result : null;
	}

}
