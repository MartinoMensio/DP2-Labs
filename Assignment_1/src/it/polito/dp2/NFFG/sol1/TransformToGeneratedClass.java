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
public class TransformToGeneratedClass implements Transformer<NffgVerifier, Verifier> {

	private NffgVerifier input;

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
	 * @return an object belonging to the Transformer interface
	 */
	public static Transformer<NffgVerifier, Verifier> newTransformer(NffgVerifier input) {
		// TODO Auto-generated method stub
		return new TransformToGeneratedClass(input);
	}

	/**
	 * implements the transformation of the root element (verifier)
	 */
	@Override
	public Verifier transform() {
		Verifier v = new Verifier();
		// get the live list of nffgs
		List<NffgT> nffg_list = v.getNffg();
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
	private NffgT transformNffg(NffgReader nffgR) {
		// create an empty nffg element
		NffgT nffg = new NffgT();
		// set the name
		nffg.setName(nffgR.getName());
		// set the time
		nffg.setUpdated(Utils.XMLGregorianCalendarFromCalendar(nffgR.getUpdateTime()));

		// nodes
		NffgT.Nodes nodes = new NffgT.Nodes();
		// get the live list
		List<NodeT> node_list = nodes.getNode();
		// add to the live list the nodes
		node_list.addAll(nffgR.getNodes()
				// the transformation of nodes can be performed in parallel
				// (same motivation as above)
				.parallelStream()
				// perform transformation of the node
				.map(nodeR -> transformNode(nodeR)).collect(Collectors.toList()));

		nffg.setNodes(nodes);

		// links
		NffgT.Links links = new NffgT.Links();
		// get the live list
		List<LinkT> link_list = links.getLink();
		link_list.addAll(nffgR.getNodes().parallelStream()
				// from a stream of nodes to a stream of links
				// NodeReader::getLinks provides links that have this node as
				// the source node (no duplicated links)
				.flatMap(nodeR -> nodeR.getLinks().stream())
				// transform the link
				.map(linkR -> transformLink(linkR)).collect(Collectors.toList()));

		nffg.setLinks(links);

		// policies
		NffgT.Policies policies = new NffgT.Policies();
		// get the live list
		List<PolicyT> policy_list = policies.getPolicy();
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
	private NodeT transformNode(NodeReader nodeR) {
		NodeT node = new NodeT();
		// set the name
		node.setName(nodeR.getName());
		// and the functionality
		node.setFunctionality(FunctionalityT.fromValue(nodeR.getFuncType().value()));
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
	private LinkT transformLink(LinkReader linkR) {
		LinkT link = new LinkT();
		// set the name
		link.setName(linkR.getName());

		NodeRefT src = new NodeRefT();
		NodeRefT dst = new NodeRefT();
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
	private PolicyT transformPolicy(PolicyReader policyR) {
		PolicyT policy = new PolicyT();
		// set the name
		policy.setName(policyR.getName());
		// and the expected result
		policy.setExpected(policyR.isPositive());
		// and the actual result
		policy.setResult(transformResult(policyR.getResult()));

		NodeRefT src = new NodeRefT();
		NodeRefT dst = new NodeRefT();
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
			List<FunctionalityT> func_list = policy.getFunctionality();
			func_list.addAll(trav_p.getTraversedFuctionalTypes().parallelStream()
					// transform the functionality
					.map(functionality -> FunctionalityT.fromValue(functionality.value()))
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
	private ResultT transformResult(VerificationResultReader resultR) {
		// the result is optional
		if (resultR == null) {
			return null;
		}
		ResultT result = new ResultT();
		// set the last verification date
		result.setVerified(Utils.XMLGregorianCalendarFromCalendar(resultR.getVerificationTime()));
		// set the actual verification result
		result.setSatisfied(resultR.getVerificationResult());
		// and the message
		result.setContent(resultR.getVerificationResultMsg());

		return result;
	}

}
