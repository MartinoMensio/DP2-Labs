package it.polito.dp2.NFFG.sol3.client2;

import java.util.*;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2VerificationResultReader implements VerificationResultReader {

	private String policyName;

	@Override
	public PolicyReader getPolicy() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}
		return null;
	}

	@Override
	public Boolean getVerificationResult() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}/result to have fresh data
		// return result.verificationResult
		return null;
	}

	@Override
	public String getVerificationResultMsg() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}/result to have fresh data
		// return result.verificationResult
		return null;
	}

	@Override
	public Calendar getVerificationTime() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}/result to have fresh data
		// return result.verificationTime
		return null;
	}

}
