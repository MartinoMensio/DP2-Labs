package it.polito.dp2.NFFG.sol1;

import java.util.*;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the VerificationResultReader interface
 * 
 * @author Martino Mensio
 *
 */
public class Sol1VerificationResultReader implements VerificationResultReader {

	private PolicyReader policy;
	private boolean result;
	private String message;
	private Calendar verificationTime;

	public Sol1VerificationResultReader(Boolean result, String message, Calendar verificationTime) {
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
	 */
	void setPolicy(PolicyReader policy) {
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