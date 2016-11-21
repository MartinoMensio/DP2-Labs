package it.polito.dp2.NFFG.lab2.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.NoGraphException;
import it.polito.dp2.NFFG.lab2.ReachabilityTester;
import it.polito.dp2.NFFG.lab2.ReachabilityTesterException;
import it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory;
import it.polito.dp2.NFFG.lab2.ServiceException;
import it.polito.dp2.NFFG.lab2.UnknownNameException;

import it.polito.dp2.NFFG.lab2.tests.ClientTest;

import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;


public class NFFGTests {

	private static NffgVerifier referenceNffgVerifier;	// reference data generator
	private static ReachabilityTester testReachabilityTester;	// reference data generator
	private static long testcase;
	private static URL serviceUrl; 
	private static String referenceNFFGName;
	private static int referenceNFFGNodeSize;
	private static ClientTest client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Create reference data generator
		System.setProperty("it.polito.dp2.NFFG.NffgVerifierFactory", "it.polito.dp2.NFFG.Random.NffgVerifierFactoryImpl");
		referenceNffgVerifier = NffgVerifierFactory.newInstance().newNffgVerifier();

		// set referenceNFFGName
		if(referenceNffgVerifier.getNffgs().size()!=0){
			TreeSet<NffgReader> rts = new TreeSet<NffgReader>(new NamedEntityReaderComparator());
			rts.addAll(referenceNffgVerifier.getNffgs());
			Iterator<NffgReader> iter = rts.iterator();
			if(iter.hasNext()){
				NffgReader graph = iter.next();
				referenceNFFGName = graph.getName();
				referenceNFFGNodeSize = graph.getNodes().size();
			}
			System.out.println("DEBUG: referenceNFFGName: "+referenceNFFGName);
			System.out.println("DEBUG: referenceNFFGNodeSize: "+referenceNFFGNodeSize);
		}

		// read testcase property
		Long testcaseObj = Long.getLong("it.polito.dp2.NFFG.Random.testcase");
		if (testcaseObj == null)
			testcase = 0;
		else
			testcase = testcaseObj.longValue();

		//set to the right URL
		// serviceUrl =  new URL("http://localhost:8080/Neo4JXML/rest");
		client = new ClientTest();
	}

	@Before
	public void setUp() throws Exception {
	}

	// method for comparing two non-null strings    
	private void compareString(String rs, String ts, String meaning) {
		assertNotNull("NULL "+meaning, ts);
		assertEquals("Wrong "+meaning, rs, ts);		
	}

	private void createClient() throws ReachabilityTesterException {
		// Create client under test
		// System.setProperty("it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory", "it.polito.dp2.NFFG.sol2.ReachabilityTesterFactory");
		// System.setProperty("it.polito.dp2.NFFG.lab2.URL", url.toString());
		try {
			testReachabilityTester = ReachabilityTesterFactory.newInstance().newReachabilityTester();
		} catch (FactoryConfigurationError fce) {
			fce.printStackTrace();
		}
		assertNotNull("Internal tester error during test setup: null reference", referenceNffgVerifier);
		assertNotNull("Could not run test: the implementation under test generated a null ReachabilityTester", testReachabilityTester);
	}

	@Test
	public final void testLoad() {
		System.out.println("DEBUG: starting testload");
		try {
			// create client with the right URL
			createClient();
			assertNull("Current graph name should be null", testReachabilityTester.getCurrentGraphName());
			// load an existing NFFG with name referenceNFFGName and check the number of links in the service
			assertNotNull("Internal error: reference NFFG name is null",referenceNFFGName);
			testReachabilityTester.loadNFFG(referenceNFFGName);
			assertEquals(client.testLoadNFFG(), referenceNFFGNodeSize);

			// check the right name results
			compareString(referenceNFFGName, testReachabilityTester.getCurrentGraphName(), "graph name");
		} catch (ReachabilityTesterException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		} catch (UnknownNameException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		} catch (ServiceException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		}
	}

	@Test
	public final void testReachability() {
		System.out.println("DEBUG: starting testReachability");
		try {
			// create client with the right URL
			createClient();
			// load an existing NFFG with name referenceNFFGName and call testReachability using nodes that belong to the graph
			assertNotNull(referenceNFFGName);
			testReachabilityTester.loadNFFG(referenceNFFGName);

			String srcNodeName="", destNodeName="";
			
			TreeSet<NodeReader> rts = new TreeSet<NodeReader>(new NamedEntityReaderComparator());
			rts.addAll(referenceNffgVerifier.getNffg(referenceNFFGName).getNodes());
			Iterator<NodeReader> iter = rts.iterator();
			while(iter.hasNext()){
				NodeReader node = iter.next();
				if((node.getFuncType() == FunctionalType.MAIL_CLIENT)||(node.getFuncType() == FunctionalType.WEB_CLIENT)){
					srcNodeName = node.getName();
					break;
				}
			}
			iter =rts.iterator();
			while(iter.hasNext()){
				NodeReader node = iter.next();
				if((node.getFuncType() == FunctionalType.MAIL_SERVER)||(node.getFuncType() == FunctionalType.WEB_SERVER)){
					destNodeName = node.getName();
					break;
				}
			}
			
			boolean resultTester=testReachabilityTester.testReachability(srcNodeName, destNodeName);
			boolean resultClient=client.testReachability(srcNodeName, destNodeName);
			assertEquals(resultTester, resultClient);

		} catch (ReachabilityTesterException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		} catch (UnknownNameException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		} catch (ServiceException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		} catch (NoGraphException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		}
	}

	@Test(expected = UnknownNameException.class)
	public final void testWrongReachability() throws ReachabilityTesterException, UnknownNameException, ServiceException, NoGraphException {
		System.out.println("DEBUG: starting testWrongReachability");
			// create client with the right URL
			createClient();
			
			assertNotNull(referenceNFFGName);
			testReachabilityTester.loadNFFG(referenceNFFGName);

			// load an existing NFFG with name referenceNFFGName and call testReachability using nodes that do not belong to the graph
			testReachabilityTester.testReachability("nonExistingSrcNode", "nonExistingSrcNode");
	}

	@Test(expected = UnknownNameException.class)
	public final void testWrongLoad() throws ReachabilityTesterException, UnknownNameException, ServiceException {
		System.out.println("DEBUG: starting testWrongLoad");
			// create client with the right URL
			createClient();
			// try to load a non-existing NFFG 
			testReachabilityTester.loadNFFG("nonexistingNFFGName");
	}


}

class NamedEntityReaderComparator implements Comparator<NamedEntityReader> {
    public int compare(NamedEntityReader f0, NamedEntityReader f1) {
    	return f0.getName().compareTo(f1.getName());
    }
}
