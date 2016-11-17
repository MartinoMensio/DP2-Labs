package it.polito.dp2.NFFG.sol1;

import java.util.*;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

public class TransformFromGeneratedClass implements Transformer<Verifier, NffgVerifier> {

	private Verifier input;

	private TransformFromGeneratedClass(Verifier input) {
		this.input = input;
	}

	public static Transformer<Verifier, NffgVerifier> newTransformer(Verifier input) {
		return new TransformFromGeneratedClass(input);
	}

	@Override
	public NffgVerifier transform() {
		MyNffgVerifier verifier = new MyNffgVerifier();
		input.getNffg().forEach(nffg -> {
			NffgReader nffgR = transformNffg(nffg);
			verifier.addNffg(nffgR);
			nffg.getPolicies().getPolicy().forEach(p -> {
				verifier.addPolicy(nffg.getName(), transformPolicy(p, nffgR));
			});
		});

		return verifier;
	}

	private NffgReader transformNffg(NffgT nffg_el) {
		MyNffgReader nffgR = new MyNffgReader(nffg_el.getName(),
				Utils.CalendarFromXMLGregorianCalendar(nffg_el.getUpdated()));
		nffg_el.getNodes().getNode().forEach(n -> {
			nffgR.addNode(transformNode(n));
		});
		nffg_el.getLinks().getLink().forEach(l -> {
			MyNodeReader src = nffgR.getMyNode(l.getSrc().getRef());
			NodeReader dst = nffgR.getNode(l.getDst().getRef());
			LinkReader linkR = new MyLinkReader(l.getName(), src, dst);
			src.addOutgoingLink(linkR);
		});

		return nffgR;
	}

	private NodeReader transformNode(NodeT node) {
		return new MyNodeReader(node.getName(), FunctionalType.fromValue(node.getFunctionality().value()));
	}

	private PolicyReader transformPolicy(PolicyT p, NffgReader nffgR) {
		NodeReader src = nffgR.getNode(p.getSrc().getRef());
		NodeReader dst = nffgR.getNode(p.getDst().getRef());
		MyVerificationResultReader result = null;
		if (p.getResult() != null) {
			result = transformResult(p.getResult());
		}
		MyPolicyReader policy;
		if (p.getFunctionality().isEmpty()) {
			policy = new MyReachabilityPolicyReader(p.getName(), nffgR, result, p.isExpected(), src, dst);
		} else {
			policy = new MyTraversalPolicyReader(p.getName(), nffgR, result, p.isExpected(), src, dst,
					transformFunctionalities(p.getFunctionality()));
		}
		if (p.getResult() != null) {
			result.setPolicy(policy);
		}
		return policy;
	}

	private MyVerificationResultReader transformResult(ResultT res) {
		return new MyVerificationResultReader(res.isSatisfied(), res.getContent(),
				Utils.CalendarFromXMLGregorianCalendar(res.getVerified()));
	}

	private Set<FunctionalType> transformFunctionalities(List<FunctionalityT> l) {
		return l.stream().map(a -> {
			return FunctionalType.fromValue(a.value());
		}).collect(Collectors.toSet());
	}

}
