package it.polito.dp2.NFFG.sol3.client2;

import java.util.*;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.client2.library.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

public class PolicyTransformer implements ThrowingTransformer<Policy, PolicyReaderImpl, RuntimeException> {

	private NffgReaderImpl nffgR;

	private PolicyTransformer(NffgReaderImpl nffgR) {
		this.nffgR = nffgR;
	}
	
	public static ThrowingTransformer<Policy, PolicyReaderImpl, RuntimeException> newNffgTransformer(NffgReaderImpl nffgR) {
		return new PolicyTransformer(nffgR);
	}

	@Override
	public PolicyReaderImpl transform(Policy policy) {
		NodeReader src = nffgR.getNode(policy.getSrc().getRef());
		NodeReader dst = nffgR.getNode(policy.getDst().getRef());
		VerificationResultReaderImpl result = null;
		// if there is a result, transform it
		if (policy.getResult() != null) {
			result = transformResult(policy.getResult());
		}
		PolicyReaderImpl policyR;
		// if some functionality need to be traversed it is a traversal policy,
		// otherwise this is a reachability policy
		if (policy.getFunctionality().isEmpty()) {
			policyR = new ReachabilityPolicyReaderImpl(policy.getName(), nffgR, result, policy.isPositive(), src,
					dst);
		} else {
			policyR = new TraversalPolicyReaderImpl(policy.getName(), nffgR, result, policy.isPositive(), src, dst,
					transformFunctionalities(policy.getFunctionality()));
		}
		if (policy.getResult() != null) {
			// add the circular reference (thanks to the fact that this is a
			// VerificationReaderImpl object)
			result.setPolicy(policyR);
		}
		return policyR;
	}

	private VerificationResultReaderImpl transformResult(Result result) {
		return new VerificationResultReaderImpl(result.isSatisfied(), result.getContent(),
				Utils.CalendarFromXMLGregorianCalendar(result.getVerified()));
	}

	private Set<FunctionalType> transformFunctionalities(List<Functionality> list) {
		return list.stream().map(a -> {
			return FunctionalType.fromValue(a.value());
		}).collect(Collectors.toSet());
	}

}
