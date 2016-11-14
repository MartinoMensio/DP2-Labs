package it.polito.dp2.NFFG.sol1;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import javax.xml.bind.*;
import javax.xml.validation.*;

import org.xml.sax.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

public class NffgVerifierFactory extends it.polito.dp2.NFFG.NffgVerifierFactory {

	@Override
	public NffgVerifier newNffgVerifier() throws NffgVerifierException {

		String fileName = System.getProperty("it.polito.dp2.NFFG.sol1.NffgInfo.file");

		Verifier verifier = null;

		try {
			JAXBContext jc = JAXBContext.newInstance("it.polito.dp2.NFFG.sol1.jaxb");
			Unmarshaller u = jc.createUnmarshaller();

			verifier = new Verifier();

			SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				u.setSchema(sf.newSchema(new File("xsd/nffgInfo.xsd")));
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			verifier = (Verifier) u.unmarshal(new FileInputStream(fileName));
			/*
			 * verifier = v.getNffg().forEach(nffg_x -> {
			 * System.out.println(nffg_x.getName()); // TODO nffg_x.get return
			 * });
			 */
		} catch (Exception e) {
			// TODO: handle exception
		}
		return unmarshalVerifier(verifier);
	}

	NffgVerifier unmarshalVerifier(Verifier v) {
		MyNffgVerifier verifier = new MyNffgVerifier();
		v.getNffg().forEach(nffg -> {
			NffgReader nffgR = unmarshalNffg(nffg);
			verifier.addNffg(nffgR);
			nffg.getPolicies().getPolicy().forEach(p -> {
				verifier.addPolicy(nffg.getName(), unmarshalPolicy(p, nffgR));
			});
		});

		return verifier;
	}

	NffgReader unmarshalNffg(NffgT nffg_el) {
		MyNffgReader nffgR = new MyNffgReader(nffg_el.getName(),
				Utils.CalendarFromXMLGregorianCalendar(nffg_el.getUpdated()));
		nffg_el.getNodes().getNode().forEach(n -> {
			nffgR.addNode(unmarshalNode(n));
		});
		nffg_el.getLinks().getLink().forEach(l -> {
			MyNodeReader src = nffgR.getMyNode(l.getSrc().getRef());
			NodeReader dst = nffgR.getNode(l.getDst().getRef());
			LinkReader linkR = new MyLinkReader(l.getName(), src, dst);
			src.addOutgoingLink(linkR);
		});

		return nffgR;
	}

	NodeReader unmarshalNode(NodeT node) {
		return new MyNodeReader(node.getName(), FunctionalType.fromValue(node.getFunctionality().value()));
	}

	PolicyReader unmarshalPolicy(PolicyT p, NffgReader nffgR) {
		NodeReader src = nffgR.getNode(p.getSrc().getRef());
		NodeReader dst = nffgR.getNode(p.getDst().getRef());
		MyVerificationResultReader result = null;
		if (p.getResult() != null) {
			result = unmarshalResult(p.getResult());
		}
		MyPolicyReader policy = new MyReachabilityPolicyReader(p.getName(), nffgR, result, p.isExpected(), src, dst);
		if (p.getResult() != null) {
			result.setPolicy(policy);
		}
		return policy;
	}

	MyVerificationResultReader unmarshalResult(ResultT res) {
		return new MyVerificationResultReader(res.isSatisfied(), res.getMessage(),
				Utils.CalendarFromXMLGregorianCalendar(res.getVerified()));
	}

}
