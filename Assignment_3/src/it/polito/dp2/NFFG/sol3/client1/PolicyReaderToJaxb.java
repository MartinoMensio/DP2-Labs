package it.polito.dp2.NFFG.sol3.client1;

import java.util.function.Function;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * This class takes a PolicyReader object and returns a corresponding object
 * belonging to the generated class Policy
 * 
 * @author Martino Mensio
 *
 */
public class PolicyReaderToJaxb implements Function<PolicyReader, Policy> {

	private ObjectFactory factory = new ObjectFactory();

	public ObjectFactory getFactory() {
		return factory;
	}

	@Override
	public Policy apply(PolicyReader policyR) {
		// all the policies are of type reachability
		ReachabilityPolicyReader reachPolicyR = (ReachabilityPolicyReader) policyR;
		Policy policy = factory.createPolicy();
		policy.setName(policyR.getName());
		policy.setNffg(policyR.getNffg().getName());
		policy.setPositive(policyR.isPositive());
		NodeRef src = factory.createNodeRef();
		src.setRef(reachPolicyR.getSourceNode().getName());
		policy.setSrc(src);
		NodeRef dst = factory.createNodeRef();
		dst.setRef(reachPolicyR.getSourceNode().getName());
		policy.setDst(dst);
		VerificationResultReader resultR = policyR.getResult();
		if (resultR != null) {
			Result result = factory.createResult();
			result.setContent(resultR.getVerificationResultMsg());
			result.setSatisfied(resultR.getVerificationResult());
			result.setVerified(Utils.XMLGregorianCalendarFromCalendar(resultR.getVerificationTime()));
			policy.setResult(result);
		}
		return policy;
	}

}
