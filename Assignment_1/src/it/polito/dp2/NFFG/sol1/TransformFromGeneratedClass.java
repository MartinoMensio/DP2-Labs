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
public class TransformFromGeneratedClass implements Transformer<Verifier, NffgVerifier> {

	private Verifier input;

	/**
	 * The constructor is private, use instead the factory method
	 * 
	 * @param input
	 *            the Verifier object to be transformed
	 */
	private TransformFromGeneratedClass(Verifier input) {
		this.input = input;
	}

	/**
	 * The static factory method
	 * 
	 * @param input
	 *            the Verifier object to be transformed
	 * @return an object belonging to the Transformer interface
	 */
	public static Transformer<Verifier, NffgVerifier> newTransformer(Verifier input) {
		return new TransformFromGeneratedClass(input);
	}

	/**
	 * implements the transformation of the root element (verifier)
	 */
	@Override
	public NffgVerifier transform() {
		NffgVerifierImpl verifier = new NffgVerifierImpl();
		try {
			input.getNffg().forEach(nffg -> {
				NffgReader nffgR = transformNffg(nffg);
				try {
					verifier.addNffg(nffgR);
					nffg.getPolicies().getPolicy().forEach(p -> {
						try {
							verifier.addPolicy(nffg.getName(), transformPolicy(p, nffgR));
						} catch (NffgVerifierException e) {
							// to go through lambda must use an unchecked exception
							throw new RuntimeException(e);
						}
					});
				} catch (Exception e) {
					// to go through lambda must use an unchecked exception
					throw new RuntimeException(e);
				}

			});
		} catch (Exception e2) {
			// this catches all exceptions
			System.err.println("Error in transformation:");
			System.err.println(e2.getMessage());
			return null;
		}

		return verifier;
	}

	/**
	 * Performs the transformation from object belonging to the class NffgT to
	 * the class NffgReader
	 * 
	 * @param nffg
	 *            the NffgT object from unmarshaling
	 * @return the NffgReader object
	 */
	private NffgReader transformNffg(Nffg nffg) {
		NffgReaderImpl nffgR = new NffgReaderImpl(nffg.getName(),
				Utils.CalendarFromXMLGregorianCalendar(nffg.getUpdated()));
		// process nodes
		nffg.getNodes().getNode().forEach(n -> {
			try {
				nffgR.addNode(transformNode(n));
			} catch (NffgVerifierException e) {
				// to go through lambda must use an unchecked exception
				throw new RuntimeException(e);
			}
		});
		// and process links
		nffg.getLinks().getLink().forEach(l -> {
			// get references to the source and destination nodes.
			// The source must be later modified, so need to use the getNodeReaderImpl
			// method
			NodeReaderImpl src = nffgR.getNodeReaderImpl(l.getSrc().getRef());
			NodeReader dst = nffgR.getNode(l.getDst().getRef());
			// build the link
			LinkReader linkR = new LinkReaderImpl(l.getName(), src, dst);
			// add the circular reference to the getNodeReaderImpl
			try {
				src.addOutgoingLink(linkR);
			} catch (NffgVerifierException e) {
				// to go through lambda must use an unchecked exception
				throw new RuntimeException(e);
			}
		});

		return nffgR;
	}

	/**
	 * Performs the transformation from object belonging to the class NodeT to
	 * the class NodeReader
	 * 
	 * @param node
	 *            the NodeT object from unmarshaling
	 * @return the NodeReader object
	 */
	private NodeReader transformNode(Node node) {
		return new NodeReaderImpl(node.getName(), FunctionalType.fromValue(node.getFunctionality().value()));
	}

	/**
	 * Performs the transformation from object belonging to the class PolicyT to
	 * the class PolicyReader
	 * 
	 * @param policy
	 *            the NffgT object from unmarshaling
	 * @return the PolicyReader object
	 */
	private PolicyReader transformPolicy(Policy policy, NffgReader nffgR) {
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
	 * Performs the transformation from object belonging to the class ResultT to
	 * the class VerificationResultReaderImpl. Note that returns an object
	 * belonging to the class that I implemented, so that i can call the method
	 * setPolicy to add the circular reference
	 * 
	 * @param result
	 *            the ResultT object from unmarshaling
	 * @return the VerificationResultReaderImpl object
	 */
	private VerificationResultReaderImpl transformResult(Result result) {
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
