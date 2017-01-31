package it.polito.dp2.NFFG.sol3.client2;

import java.util.*;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.client2.library.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * This class takes a Policy object and returns a corresponding object belonging
 * to the PolicyReaderImpl class
 * 
 * @author Martino Mensio
 *
 */
public class PolicyTransformer implements ThrowingTransformer<Policy, PolicyReaderImpl, NffgVerifierException> {

	private NffgReaderImpl nffgR;

	private PolicyTransformer(NffgReaderImpl nffgR) {
		this.nffgR = nffgR;
	}

	public static ThrowingTransformer<Policy, PolicyReaderImpl, NffgVerifierException> newNffgTransformer(
			NffgReaderImpl nffgR) {
		return new PolicyTransformer(nffgR);
	}

	@Override
	public PolicyReaderImpl transform(Policy policy) throws NffgVerifierException {
		VerificationResultReaderImpl result = null;
		// if there is a result, transform it
		if (policy.getResult() != null) {
			result = transformResult(policy.getResult());
		}
		PolicyReaderImpl policyR = null;
		Reachability reachability = policy.getSpecification().getReachability();
		Isolation isolation = policy.getSpecification().getIsolation();
		if (reachability != null) {
			NodeReader src = nffgR.getNode(reachability.getSrc().getRef());
			NodeReader dst = nffgR.getNode(reachability.getDst().getRef());
			// if some functionality need to be traversed it is a traversal
			// policy,
			// otherwise this is a reachability policy
			if (reachability.getFunctionality().isEmpty()) {
				policyR = new ReachabilityPolicyReaderImpl(policy.getName(), nffgR, result, policy.isPositive(), src,
						dst);
			} else {
				policyR = new TraversalPolicyReaderImpl(policy.getName(), nffgR, result, policy.isPositive(), src, dst,
						transformFunctionalities(reachability.getFunctionality()));
			}
			if (policy.getResult() != null) {
				// add the circular reference (thanks to the fact that this is a
				// VerificationReaderImpl object)
				result.setPolicy(policyR);
			}
		}
		else if (isolation != null) {
			List<NodeReader> firstSet = isolation.getFirstSet().stream().map(n -> nffgR.getNode(n.getRef())).collect(Collectors.toList());
			List<NodeReader> secondSet = isolation.getSecondSet().stream().map(n -> nffgR.getNode(n.getRef())).collect(Collectors.toList());
			policyR = new IsolationPolicyReaderImpl(policy.getName(), nffgR, result, policy.isPositive(), firstSet,
					secondSet);
		}
		return policyR;
	}

	private VerificationResultReaderImpl transformResult(Result result) throws NffgVerifierException {
		return new VerificationResultReaderImpl(result.isSatisfied(), result.getContent(),
				Utils.CalendarFromXMLGregorianCalendar(result.getVerified()));
	}

	private Set<FunctionalType> transformFunctionalities(List<Functionality> list) {
		return list.stream().map(a -> {
			return FunctionalType.fromValue(a.value());
		}).collect(Collectors.toSet());
	}

}
