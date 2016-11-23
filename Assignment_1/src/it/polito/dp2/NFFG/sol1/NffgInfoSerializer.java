package it.polito.dp2.NFFG.sol1;

import javax.xml.bind.*;
import javax.xml.validation.*;

import org.xml.sax.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

import java.io.*;

/**
 * The serializer class takes the data from the monitor and serializes them
 * using the generated classes and marshaling the root element. The output is an
 * XML file whose name (path) is passed as a parameter on the command line. For
 * testing purposes, if the parameter is not provided, i use the standard output
 * to print the XML file
 * 
 * @author Martino Mensio
 *
 */

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

		// apply the transformation from data from interfaces to objects of the
		// generated classes
		Verifier v = TransformToGeneratedClass.newTransformer(monitor).transform();

		// serialize (+schema)

		try {
			JAXBContext jc = JAXBContext.newInstance("it.polito.dp2.NFFG.sol1.jaxb");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

			m.setSchema(sf.newSchema(new File("xsd/nffgInfo.xsd")));

			if (outFile != null) {
				m.marshal(v, new File(outFile));
			} else {
				m.marshal(v, System.out);
			}

		} catch (SAXException e) {
			// schema problem
			System.err.println("invalid schema: " + e.getMessage());
		} catch (JAXBException e) {
			// marshaling problem
			System.err.println("unexpected problem occurs during the marshalling: " + e.getMessage());
		} catch (Exception e) {
			// last chance
			System.err.println(e.getMessage());
		}
	}
}
