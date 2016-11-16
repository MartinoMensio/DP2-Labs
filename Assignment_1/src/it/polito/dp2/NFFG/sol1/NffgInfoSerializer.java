package it.polito.dp2.NFFG.sol1;

import java.util.*;

import javax.xml.bind.*;
import javax.xml.validation.*;

import org.xml.sax.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

import java.io.*;

public class NffgInfoSerializer {
	private NffgVerifier monitor;
	private String outFile = null;

	/**
	 * Default constructor. Prints to stdout unless a file name is given for
	 * output
	 * 
	 * @throws NffgVerifierException
	 */
	public NffgInfoSerializer() throws NffgVerifierException {
		// use the full path in order to call the generic factory
		it.polito.dp2.NFFG.NffgVerifierFactory factory = it.polito.dp2.NFFG.NffgVerifierFactory.newInstance();
		monitor = factory.newNffgVerifier();
	}

	/**
	 * Constructor
	 * 
	 * @param outFile
	 *            is the XML output file
	 * @throws NffgVerifierException
	 */
	public NffgInfoSerializer(String outFile) throws NffgVerifierException {
		this();
		this.outFile = outFile;
	}

	public NffgInfoSerializer(NffgVerifier monitor) {
		super();
		this.monitor = monitor;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NffgInfoSerializer wf;
		try {
			if (args.length < 1) {
				// use stdout
				wf = new NffgInfoSerializer();
			} else {
				// Try to use the provided filename
				wf = new NffgInfoSerializer(args[0]);
			}
			wf.serialize();
		} catch (NffgVerifierException e) {
			System.err.println("Could not instantiate data generator.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates the XML output from the monitor data
	 */
	public void serialize() {

		// create root element
		Verifier v = new Verifier();
		// get the live list of nffgs
		List<NffgT> nffg_list = v.getNffg();
		monitor.getNffgs().forEach(nffgR -> {
			// get the xml object for the nffg from the reader
			// and add it to the live list
			nffg_list.add(marshalNffg(nffgR));
		});

		// serialize (+schema)

		try {
			JAXBContext jc = JAXBContext.newInstance("it.polito.dp2.NFFG.sol1.jaxb");
			Marshaller m = jc.createMarshaller();

			SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

			try {
				m.setSchema(sf.newSchema(new File("xsd/nffgInfo.xsd")));
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			if (outFile != null) {
				m.marshal(v, new File(outFile));
			} else {
				m.marshal(v, System.out);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Performs the transformation from object belonging to the class NffgReader
	 * to the class NffgT
	 * 
	 * @param nffgR
	 *            the NffgReader object
	 * @return the NffgT object for marshaling
	 */
	NffgT marshalNffg(NffgReader nffgR) {
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
			node_list.add(marshalNode(nodeR));
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
					link_list.add(marshalLink(linkR));
				});
		nffg.setLinks(links);

		// policies
		NffgT.Policies policies = new NffgT.Policies();
		// get the live list
		List<PolicyT> policy_list = policies.getPolicy();
		// get the policies for the specific nffg
		monitor.getPolicies(nffgR.getName()).forEach(policyR -> {
			policy_list.add(marshalPolicy(policyR));
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
	NodeT marshalNode(NodeReader nodeR) {
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
	LinkT marshalLink(LinkReader linkR) {
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
	PolicyT marshalPolicy(PolicyReader policyR) {
		PolicyT policy = new PolicyT();
		// set the name
		policy.setName(policyR.getName());
		policy.setExpected(policyR.isPositive());
		policy.setResult(marshalResult(policyR.getResult()));
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

	ResultT marshalResult(VerificationResultReader resultR) {
		if (resultR == null) {
			return null;
		}
		ResultT result = new ResultT();
		result.setVerified(Utils.XMLGregorianCalendarFromCalendar(resultR.getVerificationTime()));
		result.setSatisfied(resultR.getVerificationResult());
		result.setMessage(resultR.getVerificationResultMsg());
		return result;
	}
}
