package it.polito.dp2.NFFG.sol3.client2.library;

import java.util.*;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the TraversalPolicyReader interface
 * 
 * @author Martino Mensio
 *
 */
public class TraversalPolicyReaderImpl extends ReachabilityPolicyReaderImpl implements TraversalPolicyReader {

	// the set of functionalities to be traversed
	private Set<FunctionalType> functionalities;

	public TraversalPolicyReaderImpl(String name, NffgReader nffg, VerificationResultReader result, Boolean expected,
			NodeReader src, NodeReader dst, Set<FunctionalType> functionalities) {
		super(name, nffg, result, expected, src, dst);
		this.functionalities = functionalities;
	}

	@Override
	public Set<FunctionalType> getTraversedFuctionalTypes() {
		return functionalities;
	}

}