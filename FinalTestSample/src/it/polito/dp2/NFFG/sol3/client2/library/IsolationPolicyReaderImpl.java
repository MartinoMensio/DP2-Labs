package it.polito.dp2.NFFG.sol3.client2.library;

import java.util.*;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class IsolationPolicyReaderImpl extends PolicyReaderImpl implements IsolationPolicyReader {

	private List<NodeReader> firstSet;
	private List<NodeReader> secondSet;

	public IsolationPolicyReaderImpl(String name, NffgReader nffg, VerificationResultReader result, Boolean expected,
			List<NodeReader> firstSet, List<NodeReader> secondSet) throws NffgVerifierException {
		super(name, nffg, result, expected);
		if (firstSet == null || secondSet == null) {
			throw new NffgVerifierException("some data null when creating an IsolationPolicyReader");
		}
		this.firstSet = firstSet;
		this.secondSet = secondSet;
	}

	@Override
	public Set<NodeReader> getFirstNodeSet() {
		return firstSet.stream().collect(Collectors.toSet());
	}

	@Override
	public Set<NodeReader> getSecondNodeSet() {
		return secondSet.stream().collect(Collectors.toSet());
	}

}
