package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the PolicyReader interface
 * 
 * @author Martino Mensio
 *
 */
public class MyPolicyReader extends MyNamedEntityReader implements PolicyReader {

	private NffgReader nffg;
	private VerificationResultReader result;
	private boolean expected;

	public MyPolicyReader(String name, NffgReader nffg, VerificationResultReader result, Boolean expected) {
		super(name);
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
