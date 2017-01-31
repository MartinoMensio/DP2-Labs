package it.polito.dp2.NFFG.sol3.client1;

import java.util.function.Function;
import java.util.stream.Collectors;

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
		Policy policy = factory.createPolicy();
		policy.setName(policyR.getName());
		policy.setNffg(policyR.getNffg().getName());
		policy.setPositive(policyR.isPositive());
		PolicySpecification spec = factory.createPolicySpecification();
		if (policyR instanceof ReachabilityPolicyReader) {
			// reachability / traversal
			ReachabilityPolicyReader reachPolicyR = (ReachabilityPolicyReader) policyR;
			Reachability reachability = factory.createReachability();
			NodeRef src = factory.createNodeRef();
			src.setRef(reachPolicyR.getSourceNode().getName());
			reachability.setSrc(src);
			NodeRef dst = factory.createNodeRef();
			dst.setRef(reachPolicyR.getSourceNode().getName());
			reachability.setDst(dst);
			spec.setReachability(reachability);
		} else if (policyR instanceof IsolationPolicyReader) {
			IsolationPolicyReader isolationPolicyR = (IsolationPolicyReader) policyR;
			Isolation isolation = factory.createIsolation();
			isolation.getFirstSet().addAll(isolationPolicyR.getFirstNodeSet().stream().map(n -> {
				NodeRef ref = factory.createNodeRef();
				ref.setRef(n.getName());
				return ref;
			}).collect(Collectors.toSet()));
			isolation.getSecondSet().addAll(isolationPolicyR.getSecondNodeSet().stream().map(n -> {
				NodeRef ref = factory.createNodeRef();
				ref.setRef(n.getName());
				return ref;
			}).collect(Collectors.toSet()));
			spec.setIsolation(isolation);
		}
		policy.setSpecification(spec);
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
