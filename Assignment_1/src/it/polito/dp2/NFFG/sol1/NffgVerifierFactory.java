package it.polito.dp2.NFFG.sol1;

import java.io.*;

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

			return TransformFromGeneratedClass.newTransformer(verifier).transform();

		} catch (Exception e) {
			// basic exception handling
			// if some exception (e.g. NullPointerException) occur, the
			// exception is caught and the return value is null
			System.err.println(e.getMessage());
			return null;
		}

	}

}
