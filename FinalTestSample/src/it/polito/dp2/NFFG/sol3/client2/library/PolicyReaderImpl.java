package it.polito.dp2.NFFG.sol3.client2.library;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the PolicyReader interface
 * 
 * @author Martino Mensio
 *
 */
public class PolicyReaderImpl extends NamedEntityReaderImpl implements PolicyReader {

	private NffgReader nffg;
	private VerificationResultReader result;
	private boolean expected;

	public PolicyReaderImpl(String name, NffgReader nffg, VerificationResultReader result, Boolean expected)
			throws NffgVerifierException {
		super(name);
		// result is optional
		if (nffg == null || expected == null) {
			throw new NffgVerifierException("something null when creating a PolicyReader");
		}
		this.nffg = nffg;
		this.result = result;
		this.expected = expected;
	}

	@Override
	public NffgReader getNffg() {
		return nffg;
	}

	@Override
	public VerificationResultReader getResult() {
		return result;
	}

	@Override
	public Boolean isPositive() {
		return expected;
	}

}
