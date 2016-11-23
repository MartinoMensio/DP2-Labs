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

			// schema
			SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			u.setSchema(sf.newSchema(new File("xsd/nffgInfo.xsd")));

			// perform the unmarshaling
			verifier = (Verifier) u.unmarshal(new FileInputStream(fileName));

			// and transform objects from the generated classes to the
			// interfaces (personal implementation)
			return TransformFromGeneratedClass.newTransformer(verifier).transform();

		} catch (SAXException e) {
			// schema problem
			System.err.println("invalid schema: " + e.getMessage());
		} catch (JAXBException e) {
			// unmarshaling problem
			System.err.println("unexpected problem occurs during the unmarshalling: " + e.getMessage());
		} catch (Exception e) {
			// last chance
			System.err.println(e.getMessage());
		}
		return null;
	}

}
