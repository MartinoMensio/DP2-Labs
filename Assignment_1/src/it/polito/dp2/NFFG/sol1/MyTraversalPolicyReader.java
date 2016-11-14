package it.polito.dp2.NFFG.sol1;

import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.TraversalPolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;

public class MyTraversalPolicyReader extends MyReachabilityPolicyReader implements TraversalPolicyReader {

	private Set<FunctionalType> functionalities;

	public MyTraversalPolicyReader(String name, NffgReader nffg, VerificationResultReader result, Boolean expected,
			NodeReader src, NodeReader dst, Set<FunctionalType> functionalities) {
		super(name, nffg, result, expected, src, dst);
		this.functionalities = functionalities;
	}

	@Override
	public Set<FunctionalType> getTraversedFuctionalTypes() {
		return functionalities;
	}

}
