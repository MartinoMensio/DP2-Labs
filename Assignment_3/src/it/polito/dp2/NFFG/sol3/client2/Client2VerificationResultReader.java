package it.polito.dp2.NFFG.sol3.client2;

import java.util.*;

import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2VerificationResultReader implements VerificationResultReader {

	private String policyName;
	private boolean satisfied;
	private String content;
	private Calendar verified;

	Client2VerificationResultReader(String policyName, boolean satisfied, String content, XMLGregorianCalendar verified) {
		// TODO Auto-generated constructor stub
		this.policyName = policyName;
		this.satisfied = satisfied;
		this.content = content;
		this.verified = verified.toGregorianCalendar();
	}

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
		return satisfied;
	}

	@Override
	public String getVerificationResultMsg() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}/result to have fresh data
		// return result.verificationResult
		return content;
	}

	@Override
	public Calendar getVerificationTime() {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}/result to have fresh data
		// return result.verificationTime
		return verified;
	}

}
