package it.polito.dp2.NFFG.sol1;

import java.util.*;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

/**
 * This class implements the transformation for objects belonging to the class
 * it.polito.dp2.NFFG.NffgVerifier and transforms them into objects belonging to
 * the interface it.polito.dp2.NFFG.sol1.jaxb.Verifier
 * 
 * @author Martino Mensio
 *
 */
public class TransformToGeneratedClass implements ThrowingTransformer<NffgVerifier, Verifier, RuntimeException> {

	private NffgVerifier input;

	private ObjectFactory factory = new ObjectFactory();

	/**
	 * The constructor is private, use instead the factory method
	 * 
	 * @param input
	 *            the NffgVerifier object to be transformed
	 */
	private TransformToGeneratedClass(NffgVerifier input) {
		this.input = input;
	}

	/**
	 * The static factory method
	 * 
	 * @param input
	 *            the NffgVerifier object to be transformed
	 * @return an object belonging to the ThrowingTransformer interface
	 */
	public static ThrowingTransformer<NffgVerifier, Verifier, RuntimeException> newTransformer(NffgVerifier input) {
		// TODO Auto-generated method stub
		return new TransformToGeneratedClass(input);
	}

	/**
	 * implements the transformation of the root element (verifier)
	 */
	@Override
	public Verifier transform() {
		Verifier v = factory.createVerifier();
		// get the live list of nffgs
		List<Nffg> nffg_list = v.getNffg();
		// add the nffgs to the live list
		nffg_list.addAll(input.getNffgs()
				// use parallel stream to possibly perform parallel execution of
				// transformation. This can be done because the operations done
				// have no side-effects and operate on different data (both
				// input and output). This can lead to better utilization of
				// resources and could fasten the transform operations.
				.parallelStream()
				// transform the nffg
				.map(nffgR -> transformNffg(nffgR)).collect(Collectors.toList()));

		return v;
	}

	/**
	 * Performs the transformation from object belonging to the class NffgReader
	 * to the class NffgT
	 * 
	 * @param nffgR
	 *            the NffgReader object
	 * @return the NffgT object for marshaling
	 */
	private Nffg transformNffg(NffgReader nffgR) {
		// create an empty nffg element
		Nffg nffg = factory.createNffg();
		// set the name
		nffg.setName(nffgR.getName());
		// set the time
		nffg.setUpdated(Utils.XMLGregorianCalendarFromCalendar(nffgR.getUpdateTime()));

		// nodes
		Nffg.Nodes nodes = factory.createNffgNodes();
		// get the live list
		List<Node> node_list = nodes.getNode();
		// add to the live list the nodes
		node_list.addAll(nffgR.getNodes()
				// the transformation of nodes can be performed in parallel
				// (same motivation as above)
				.parallelStream()
				// perform transformation of the node
				.map(nodeR -> transformNode(nodeR)).collect(Collectors.toList()));

		nffg.setNodes(nodes);

		// links
		Nffg.Links links = factory.createNffgLinks();
		// get the live list
		List<Link> link_list = links.getLink();
		link_list.addAll(nffgR.getNodes().parallelStream()
				// from a stream of nodes to a stream of links
				// NodeReader::getLinks provides links that have this node as
				// the source node (no duplicated links)
				.flatMap(nodeR -> nodeR.getLinks().stream())
				// transform the link
				.map(linkR -> transformLink(linkR)).collect(Collectors.toList()));

		nffg.setLinks(links);

		// policies
		Nffg.Policies policies = factory.createNffgPolicies();
		// get the live list
		List<Policy> policy_list = policies.getPolicy();
		// get the policies for the specific nffg
		policy_list.addAll(input.getPolicies(nffgR.getName()).parallelStream()
				// transform the policy
				.map(policyR -> transformPolicy(policyR)).collect(Collectors.toList()));

		nffg.setPolicies(policies);

		return nffg;
	}

	/**
	 * Performs the transformation from object belonging to the class NodeReader
	 * to the class NodeT
	 * 
	 * @param nodeR
	 *            the NodeReader object
	 * @return the NodeT object for marshaling
	 */
	private Node transformNode(NodeReader nodeR) {
		Node node = factory.createNode();
		// set the name
		node.setName(nodeR.getName());
		// and the functionality
		node.setFunctionality(Functionality.fromValue(nodeR.getFuncType().value()));
		return node;
	}

	/**
	 * Performs the transformation from object belonging to the class LinkReader
	 * to the class LinkT
	 * 
	 * @param linkR
	 *            the LinkReader object
	 * @return the LinkT object for marshaling
	 */
	private Link transformLink(LinkReader linkR) {
		Link link = factory.createLink();
		// set the name
		link.setName(linkR.getName());

		NodeRef src = factory.createNodeRef();
		NodeRef dst = factory.createNodeRef();
		// build the source
		src.setRef(linkR.getSourceNode().getName());
		link.setSrc(src);
		// and the destination
		dst.setRef(linkR.getDestinationNode().getName());
		link.setDst(dst);

		return link;
	}

	/**
	 * 
	 * Performs the transformation from object belonging to the class
	 * PolicyReader to the class PolicyT
	 * 
	 * @param policyR
	 *            the PolicyReader object
	 * @return the PolicyT object for marshaling
	 */
	private Policy transformPolicy(PolicyReader policyR) {
		Policy policy = factory.createPolicy();
		// set the name
		policy.setName(policyR.getName());
		// and the expected result
		policy.setPositive(policyR.isPositive());
		// and the actual result
		policy.setResult(transformResult(policyR.getResult()));

		NodeRef src = factory.createNodeRef();
		NodeRef dst = factory.createNodeRef();
		// TraversalPolicyReader is a subclass of ReachabilityPolicyReader
		ReachabilityPolicyReader reach_p = (ReachabilityPolicyReader) policyR;
		// set the source
		src.setRef(reach_p.getSourceNode().getName());
		policy.setSrc(src);
		// and the destination
		dst.setRef(reach_p.getDestinationNode().getName());
		policy.setDst(dst);

		// for traversal policies, also add the functionalities to be traversed
		if (policyR instanceof TraversalPolicyReader) {
			TraversalPolicyReader trav_p = (TraversalPolicyReader) policyR;
			List<Functionality> func_list = policy.getFunctionality();
			func_list.addAll(trav_p.getTraversedFuctionalTypes().parallelStream()
					// transform the functionality
					.map(functionality -> Functionality.fromValue(functionality.value()))
					.collect(Collectors.toList()));
		}

		return policy;
	}

	/**
	 * 
	 * Performs the transformation from object belonging to the class
	 * VerificationResultReader to the class ResultT
	 * 
	 * @param policyR
	 *            the PolicyReader object
	 * @return the PolicyT object for marshaling
	 */
	private Result transformResult(VerificationResultReader resultR) {
		// the result is optional
		if (resultR == null) {
			return null;
		}
		Result result = factory.createResult();
		// set the last verification date
		result.setVerified(Utils.XMLGregorianCalendarFromCalendar(resultR.getVerificationTime()));
		// set the actual verification result
		result.setSatisfied(resultR.getVerificationResult());
		// and the message
		result.setContent(resultR.getVerificationResultMsg());

		return result;
	}

}
