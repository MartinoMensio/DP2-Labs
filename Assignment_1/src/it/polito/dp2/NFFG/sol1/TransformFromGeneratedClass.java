package it.polito.dp2.NFFG.sol1;

import java.util.*;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;
import it.polito.dp2.NFFG.sol1.library.*;

/**
 * This class implements the transformation for objects belonging to the class
 * it.polito.dp2.NFFG.sol1.jaxb.Verifier and transforms them into objects
 * belonging to the interface it.polito.dp2.NFFG.NffgVerifier
 * 
 * @author Martino Mensio
 *
 */
public class TransformFromGeneratedClass implements ThrowingTransformer<Verifier, NffgVerifier, NffgVerifierException> {

	/**
	 * The constructor is private, use instead the factory method
	 * 
	 * @param input
	 *            the Verifier object to be transformed
	 */
	private TransformFromGeneratedClass() {
	}

	/**
	 * The static factory method
	 * 
	 * @param input
	 *            the Verifier object to be transformed
	 * @return an object belonging to the ThrowingTransformer interface
	 */
	public static ThrowingTransformer<Verifier, NffgVerifier, NffgVerifierException> newTransformer() {
		return new TransformFromGeneratedClass();
	}

	/**
	 * implements the transformation of the root element (verifier)
	 */
	@Override
	public NffgVerifier transform(Verifier input) throws NffgVerifierException {
		NffgVerifierImpl verifier = new NffgVerifierImpl();
		for (Nffg nffg : input.getNffg()) {
			NffgReader nffgR = transformNffg(nffg);
			// add the nffg
			verifier.addNffg(nffgR);
			for (Policy policy : nffg.getPolicies().getPolicy()) {
				// add the policy
				verifier.addPolicy(nffg.getName(), transformPolicy(policy, nffgR));
			}
		}
		return verifier;
	}

	/**
	 * Performs the transformation from object belonging to the class Nffg to
	 * the class NffgReader
	 * 
	 * @param nffg
	 *            the Nffg object from unmarshaling
	 * @return the NffgReader object
	 */
	private NffgReader transformNffg(Nffg nffg) throws NffgVerifierException {
		NffgReaderImpl nffgR = new NffgReaderImpl(nffg.getName(),
				Utils.CalendarFromXMLGregorianCalendar(nffg.getUpdated()));
		// process nodes
		for (Node node : nffg.getNodes().getNode()) {
			nffgR.addNode(transformNode(node));
		}
		// and process links
		for (Link link : nffg.getLinks().getLink()) {
			// get references to the source and destination nodes.
			// The source must be later modified, so need to use the
			// getNodeReaderImpl method
			NodeReaderImpl src = nffgR.getNodeReaderImpl(link.getSrc().getRef());
			NodeReader dst = nffgR.getNode(link.getDst().getRef());
			// build the link
			LinkReader linkR = new LinkReaderImpl(link.getName(), src, dst);
			// add the circular reference to the getNodeReaderImpl
			src.addOutgoingLink(linkR);
		}
		return nffgR;
	}

	/**
	 * Performs the transformation from object belonging to the class Node to
	 * the class NodeReader
	 * 
	 * @param node
	 *            the Node object from unmarshaling
	 * @return the NodeReader object
	 * @throws NffgVerifierException
	 */
	private NodeReader transformNode(Node node) throws NffgVerifierException {
		return new NodeReaderImpl(node.getName(), FunctionalType.fromValue(node.getFunctionality().value()));
	}

	/**
	 * Performs the transformation from object belonging to the class Policy to
	 * the class PolicyReader
	 * 
	 * @param policy
	 *            the Nffg object from unmarshaling
	 * @return the PolicyReader object
	 * @throws NffgVerifierException
	 */
	private PolicyReader transformPolicy(Policy policy, NffgReader nffgR) throws NffgVerifierException {
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
			policyR = new ReachabilityPolicyReaderImpl(policy.getName(), nffgR, result, policy.isPositive(), src, dst);
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

	/**
	 * Performs the transformation from object belonging to the class Result to
	 * the class VerificationResultReaderImpl. Note that returns an object
	 * belonging to the class that I implemented, so that i can call the method
	 * setPolicy to add the circular reference
	 * 
	 * @param result
	 *            the Result object from unmarshaling
	 * @return the VerificationResultReaderImpl object
	 * @throws NffgVerifierException
	 */
	private VerificationResultReaderImpl transformResult(Result result) throws NffgVerifierException {
		return new VerificationResultReaderImpl(result.isSatisfied(), result.getContent(),
				Utils.CalendarFromXMLGregorianCalendar(result.getVerified()));
	}

	/**
	 * Performs the transformation from a List of objects belonging to the class
	 * FunctionalityT to a Set of objects belongingto the class FunctionalType
	 * 
	 * @param list
	 *            the List<FunctionalityT> object
	 * @return the Set<FunctionalType> object
	 */
	private Set<FunctionalType> transformFunctionalities(List<Functionality> list) {
		return list.stream().map(a -> {
			return FunctionalType.fromValue(a.value());
		}).collect(Collectors.toSet());
	}

}
