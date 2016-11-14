package it.polito.dp2.NFFG.sol1;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.bind.*;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

public class MyNffgVerifier implements NffgVerifier {
	
	private Map<String,NffgReader> nffgs;
	private Map<String,Set<PolicyReader>> policies;

	public MyNffgVerifier(Map<String,NffgReader> nffgs, Map<String,Set<PolicyReader>> policies) {
		this.nffgs = nffgs;
		this.policies = policies;
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
