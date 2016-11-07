package it.polito.dp2.NFFG.sol1;

import java.util.*;
import java.util.stream.*;

import javax.xml.bind.*;
import javax.xml.datatype.*;
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
		NffgVerifierFactory factory = NffgVerifierFactory.newInstance();
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

	public void serialize() {

		Verifier v = new Verifier();
		List<NffgT> nffgs_x = v.getNffg();
		monitor.getNffgs().forEach(nffg_j -> {
			NffgT nffg_x = new NffgT();
			nffg_x.setName(nffg_j.getName());
			GregorianCalendar lastUpdate = new GregorianCalendar();
			lastUpdate.setTime(nffg_j.getUpdateTime().getTime());
			try {
				nffg_x.setLastUpdate(DatatypeFactory.newInstance().newXMLGregorianCalendar(lastUpdate));
			} catch (DatatypeConfigurationException e) {
				System.err.println("Error in lastUpdate handling: lastUpdate = " + lastUpdate);
				e.printStackTrace();
				System.exit(1);
			}
			// nodes
			NffgT.Nodes nodes_x = new NffgT.Nodes();
			List<NodeT> node_list_x = nodes_x.getNode();
			nffg_j.getNodes().forEach(node_j -> {
				NodeT node_x = new NodeT();
				node_x.setName(node_j.getName());
				node_x.setFunctionality(FunctionalityT.fromValue(node_j.getFuncType().value()));
				node_list_x.add(node_x);
			});
			nffg_x.setNodes(nodes_x);
			// links
			NffgT.Links links_x = new NffgT.Links();
			List<LinkT> link_list_x = links_x.getLink();
			// nodes
			nffg_j.getNodes().stream()
					// from a stream of nodes to a stream of links
					.flatMap((NodeReader nr) -> nr.getLinks().stream())
					// remove duplicates (using a map and reading the values
					// set)
					.collect(Collectors.toMap(LinkReader::getName, p -> p, (p, q) -> p)).values()
					// create and add a link to the list of links
					.forEach(link_j -> {
						LinkT link_x = new LinkT();
						link_x.setName(link_j.getName());
						NodeRefT src = new NodeRefT();
						src.setRef(link_j.getSourceNode().getName());
						link_x.setSrc(src);
						NodeRefT dst = new NodeRefT();
						dst.setRef(link_j.getDestinationNode().getName());
						link_x.setDst(dst);
						link_list_x.add(link_x);
					});
			nffg_x.setLinks(links_x);
			// policies
			NffgT.Policies policies_x = new NffgT.Policies();
			List<PolicyT> policy_list_x = policies_x.getPolicy();
			monitor.getPolicies(nffg_j.getName()).forEach(policy_j -> {
				PolicyT policy = new PolicyT();
				policy.setName(policy_j.getName());
				policy.setResult(policy_j.isPositive() ? ResultT.POSITIVE : ResultT.NEGATIVE);
				NodeRefT src = new NodeRefT();
				NodeRefT dst = new NodeRefT();
				// TraversalPolicyReader is a subclass of
				// ReachabilityPolicyReader
				ReachabilityPolicyReader reach_p = (ReachabilityPolicyReader) policy_j;
				src.setRef(reach_p.getSourceNode().getName());
				policy.setSrc(src);
				dst.setRef(reach_p.getDestinationNode().getName());
				policy.setDst(dst);
				if (policy_j instanceof TraversalPolicyReader) {
					TraversalPolicyReader trav_p = (TraversalPolicyReader) policy_j;
					List<FunctionalityT> func_list_x = policy.getFunctionality();
					trav_p.getTraversedFuctionalTypes().forEach(func_j -> {
						func_list_x.add(FunctionalityT.fromValue(func_j.value()));
					});
				}
				policy_list_x.add(policy);
			});

			nffg_x.setPolicies(policies_x);
			// TODO more
			nffgs_x.add(nffg_x);
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

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
