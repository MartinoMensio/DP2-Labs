package it.polito.dp2.NFFG.sol1;

import java.util.Calendar;

import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;

public class MyVerificationResultReader implements VerificationResultReader {

	private PolicyReader policy;
	private boolean result;
	private String message;
	private Calendar verificationTime;
	
	public MyVerificationResultReader(Boolean result, String message, Calendar verificationTime) {
		this.result = result;
		this.message = message;
		this.verificationTime = verificationTime;
	}
	
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
