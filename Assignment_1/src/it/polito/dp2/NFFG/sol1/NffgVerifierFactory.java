package it.polito.dp2.NFFG.sol1;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
			
			verifier = (Verifier)u.unmarshal(new FileInputStream(fileName));
			/*verifier = v.getNffg().forEach(nffg_x -> {
				System.out.println(nffg_x.getName());
				// TODO
				nffg_x.get
				return 
			});*/
		} catch (Exception e) {
			// TODO: handle exception
		}
		return unmarshalVerifier(verifier);
	}
	
	NffgVerifier unmarshalVerifier(Verifier v) {
		Map<String,NffgReader> nffgs = unmarshalNffgs(v.getNffg());
		Map<String,Set<PolicyReader>> policies = unmarshalPolicies(v.getNffg());
		return new MyNffgVerifier(nffgs, policies);
	}

	Map<String,NffgReader> unmarshalNffgs(List<NffgT> nffgs) {
		return nffgs.stream().map(nffg_el -> {
			return unmarshalNffg(nffg_el);
		}).collect(Collectors.toMap(NffgReader::getName, n -> n));
	}
	
	Map<String,Set<PolicyReader>> unmarshalPolicies(List<NffgT> nffg) {
		// TODO Auto-generated method stub
		return null;
	}
	
	NffgReader unmarshalNffg(NffgT nffg_el) {
		Map<String,NodeReader> nodes = unmarshalNodes(nffg_el.getNodes().getNode(), );
		return new MyNffgReader(nffg_el.getName(), Utils.CalendarFromXMLGregorianCalendar(nffg_el.getUpdated()), nodes);
	}
	
	Map<String,NodeReader> unmarshalNodes(NffgT nffg) {
		return nffg.getNodes().getNode().stream().map(node_el -> {
			return unmarshalNode(node_el, nffg);
		}).collect(Collectors.toMap(NodeReader::getName, n -> n));
	}
	
	NodeReader unmarshalNode(NodeT node, NffgT nffg) {
		Set<LinkReader> outgoingLinks = nffg.getLinks().getLink().stream().filter(l -> {
			return l.getSrc().getRef() == node.getName();
		}).map(l-> {
			new MyLinkReader(l.getName(), l.getSrc().getRef(), l.getDst());
		}).collect(Collectors.toSet());
		return new MyNodeReader(node.getName(),FunctionalType.fromValue(node.getFunctionality().toString()), )
	}
}
