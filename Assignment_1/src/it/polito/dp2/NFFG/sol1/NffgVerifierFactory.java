package it.polito.dp2.NFFG.sol1;

import java.io.*;

import javax.xml.bind.*;
import javax.xml.validation.*;

import org.xml.sax.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

/**
 * This factory provides a NffgVerifier object reading the data from an XML file
 * specified on the system property
 * {@code it.polito.dp2.NFFG.sol1.NffgInfo.file}
 * 
 * @author Martino Mensio
 *
 */
public class NffgVerifierFactory extends it.polito.dp2.NFFG.NffgVerifierFactory {

	@Override
	public NffgVerifier newNffgVerifier() throws NffgVerifierException {

		String fileName = System.getProperty("it.polito.dp2.NFFG.sol1.NffgInfo.file");
		if (fileName == null) {
			throw new NffgVerifierException("The system property is not set");
		}

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
			return TransformFromGeneratedClass.newTransformer().transform(verifier);

		} catch (SAXException e) {
			// schema problem
			throw new NffgVerifierException("invalid schema: " + e.getMessage());
		} catch (JAXBException e) {
			// unmarshaling problem
			throw new NffgVerifierException("unexpected problem occurs during the unmarshalling: " + e.getMessage());
		} catch (NffgVerifierException e) {
			throw e;
		} catch (Exception e) {
			// last chance for all the runtime exceptions
			throw new NffgVerifierException(e.getMessage());
		}
	}

}
