package it.polito.dp2.NFFG.sol3.client2;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

public class PolicyTransformer implements Function<Policy, Client2PolicyReader> {

	private Client2NffgReader nffgR;

	public PolicyTransformer(Client2NffgReader nffgR) {
		this.nffgR = nffgR;
	}

	@Override
	public Client2PolicyReader apply(Policy policy) {
		NodeReader src = nffgR.getNode(policy.getSrc().getRef());
		NodeReader dst = nffgR.getNode(policy.getDst().getRef());
		Client2VerificationResultReader result = null;
		// if there is a result, transform it
		if (policy.getResult() != null) {
			result = transformResult(policy.getResult());
		}
		Client2PolicyReader policyR;
		// if some functionality need to be traversed it is a traversal policy,
		// otherwise this is a reachability policy
		if (policy.getFunctionality().isEmpty()) {
			policyR = new Client2ReachabilityPolicyReader(policy.getName(), nffgR, result, policy.isPositive(), src,
					dst);
		} else {
			policyR = new Client2TraversalPolicyReader(policy.getName(), nffgR, result, policy.isPositive(), src, dst,
					transformFunctionalities(policy.getFunctionality()));
		}
		if (policy.getResult() != null) {
			// add the circular reference (thanks to the fact that this is a
			// Sol1VerificationReader object)
			result.setPolicy(policyR);
		}
		return policyR;
	}

	private Client2VerificationResultReader transformResult(Result result) {
		return new Client2VerificationResultReader(result.isSatisfied(), result.getContent(),
				Utils.CalendarFromXMLGregorianCalendar(result.getVerified()));
	}

	private Set<FunctionalType> transformFunctionalities(List<Functionality> list) {
		return list.stream().map(a -> {
			return FunctionalType.fromValue(a.value());
		}).collect(Collectors.toSet());
	}

}
