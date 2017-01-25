package it.polito.dp2.NFFG.sol1.library;

import java.util.*;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the VerificationResultReader interface
 * 
 * @author Martino Mensio
 *
 */
public class VerificationResultReaderImpl implements VerificationResultReader {

	private PolicyReader policy;
	private boolean result;
	private String message;
	private Calendar verificationTime;

	public VerificationResultReaderImpl(Boolean result, String message, Calendar verificationTime)
			throws NffgVerifierException {
		if (result == null || message == null || verificationTime == null) {
			throw new NffgVerifierException("something null when creating a VerificationResultReader");
		}
		this.result = result;
		this.message = message;
		this.verificationTime = verificationTime;
	}

	/**
	 * Sets the policy that this result belongs to. This is a circular
	 * information, since from the policy we can get the result and viceversa.
	 * For this reason this information cannot be provided inside the
	 * constructor, and to break the "chicken or the egg" problem this method
	 * has been added. This method does not belong to the interface
	 * 
	 * @param policy
	 * @throws NffgVerifierException
	 */
	public void setPolicy(PolicyReader policy) throws NffgVerifierException {
		if (policy == null) {
			throw new NffgVerifierException("cannot set null policy to VerificationResultReader");
		}
		this.policy = policy;
	}

	@Override
	public PolicyReader getPolicy() {
		return policy;
	}

	@Override
	public Boolean getVerificationResult() {
		return result;
	}

	@Override
	public String getVerificationResultMsg() {
		return message;
	}

	@Override
	public Calendar getVerificationTime() {
		return verificationTime;
	}

}
