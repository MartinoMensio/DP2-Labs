package it.polito.dp2.NFFG.sol1;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.bind.*;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

public class MyNffgVerifier implements NffgVerifier {
	
	private Map<String,NffgReader> nffgs;
	private Map<String,Set<PolicyReader>> policies;

	// TODO constructor
	public MyNffgVerifier() {
		// TODO this code must be written somewhere else (a parser??)
		// TODO Auto-generated constructor stub
		/*
		String fileName = System.getProperty("it.polito.dp2.NFFG.sol1.NffgInfo.file");
		
		// TODO unmarshal
		try {
			JAXBContext jc = JAXBContext.newInstance("it.polito.dp2.NFFG.sol1.jaxb");
			Unmarshaller u = jc.createUnmarshaller();
			
			Verifier v = new Verifier();
			
			SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				u.setSchema(sf.newSchema(new File("xsd/nffgInfo.xsd")));
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			v = (Verifier)u.unmarshal(new FileInputStream(fileName));
			verifier = v.getNffg().forEach(nffg_x -> {
				System.out.println(nffg_x.getName());
				// TODO
				nffg_x.get
				return 
			});
		} catch (Exception e) {
			// TODO: handle exception
		}*/
	}

	@Override
	public NffgReader getNffg(String nffgName) {
		return nffgs.get(nffgName);
	}

	@Override
	public Set<NffgReader> getNffgs() {
		return new HashSet<>(nffgs.values());
	}

	@Override
	public Set<PolicyReader> getPolicies() {
		Set<PolicyReader> result = policies.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
		return (result.size() > 0) ? result : null;
	}

	@Override
	public Set<PolicyReader> getPolicies(String nffgName) {
		return policies.get(nffgName);
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar verificationTime) {
		Set<PolicyReader> result = policies.values().stream().flatMap(Set::stream).filter(p -> {
			return p.getResult().getVerificationTime().compareTo(verificationTime) > 0;
		}).collect(Collectors.toSet());
		return (result.size() > 0) ? result : null;
	}

}
