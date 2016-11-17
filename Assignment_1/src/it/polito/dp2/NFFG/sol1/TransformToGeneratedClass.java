package it.polito.dp2.NFFG.sol1;

import java.util.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

public class TransformToGeneratedClass implements Transformer<NffgVerifier, Verifier> {

	private NffgVerifier input;

	private TransformToGeneratedClass(NffgVerifier input) {
		this.input = input;
	}

	public static Transformer<NffgVerifier, Verifier> newTransformer(NffgVerifier input) {
		// TODO Auto-generated method stub
		return new TransformToGeneratedClass(input);
	}

	@Override
	public Verifier transform() {
		Verifier v = new Verifier();
		// get the live list of nffgs
		List<NffgT> nffg_list = v.getNffg();
		input.getNffgs().forEach(nffgR -> {
			// get the xml object for the nffg from the reader
			// and add it to the live list
			nffg_list.add(transformNffg(nffgR));
		});
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
		nffgR.getNodes().forEach(nodeR -> {
			// add to the live list the marshaled node
			node_list.add(transformNode(nodeR));
		});
		nffg.setNodes(nodes);

		// links
		NffgT.Links links = new NffgT.Links();
		// get the live list
		List<LinkT> link_list = links.getLink();
		nffgR.getNodes().stream()
				// from a stream of nodes to a stream of links
				// NodeReader::getLinks provides links that have this node as
				// the source node (no duplicated links)
				.flatMap((NodeReader nr) -> nr.getLinks().stream())
				// create and add the links to the list of links
				.forEach(linkR -> {
					link_list.add(transformLink(linkR));
				});
		nffg.setLinks(links);

		// policies
		NffgT.Policies policies = new NffgT.Policies();
		// get the live list
		List<PolicyT> policy_list = policies.getPolicy();
		// get the policies for the specific nffg
		input.getPolicies(nffgR.getName()).forEach(policyR -> {
			policy_list.add(transformPolicy(policyR));
		});
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
		src.setRef(linkR.getSourceNode().getName());
		link.setSrc(src);
		NodeRefT dst = new NodeRefT();
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
		policy.setExpected(policyR.isPositive());
		policy.setResult(transformResult(policyR.getResult()));
		/*
		 * VerificationResultReader resultR = policyR.getResult(); if(resultR !=
		 * null) { policy.set }
		 */
		NodeRefT src = new NodeRefT();
		NodeRefT dst = new NodeRefT();
		// TraversalPolicyReader is a subclass of ReachabilityPolicyReader
		ReachabilityPolicyReader reach_p = (ReachabilityPolicyReader) policyR;
		src.setRef(reach_p.getSourceNode().getName());
		policy.setSrc(src);
		dst.setRef(reach_p.getDestinationNode().getName());
		policy.setDst(dst);
		if (policyR instanceof TraversalPolicyReader) {
			TraversalPolicyReader trav_p = (TraversalPolicyReader) policyR;
			List<FunctionalityT> func_list = policy.getFunctionality();
			trav_p.getTraversedFuctionalTypes().forEach(functionality -> {
				func_list.add(FunctionalityT.fromValue(functionality.value()));
			});
		}
		return policy;
	}

	private ResultT transformResult(VerificationResultReader resultR) {
		if (resultR == null) {
			return null;
		}
		ResultT result = new ResultT();
		result.setVerified(Utils.XMLGregorianCalendarFromCalendar(resultR.getVerificationTime()));
		result.setSatisfied(resultR.getVerificationResult());
		result.setContent(resultR.getVerificationResultMsg());
		return result;
	}

}
