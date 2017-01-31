package it.polito.dp2.NFFG.lab3.test0.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab3.NFFGClientException;
import it.polito.dp2.NFFG.lab3.ServiceException;
import it.polito.dp2.NFFG.lab3.UnknownNameException;
import it.polito.dp2.NFFG.lab3.test0.NFFGClient0;
import it.polito.dp2.NFFG.lab3.test0.NFFGClient0Factory;

import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;


public class NFFGTests0 {
	
	private static NffgVerifier referenceNffgVerifier;	// reference data generator
	private static NffgVerifier testNffgVerifier;	// data generator under test
	private static NFFGClient0 testNFFGClient;			// NFFGClient under test
	private static long testcase;
	private static URL serviceUrl; 
	private static NffgReader referenceNFFG;
	private static String referencePolicyName = "TestPolicy";
	private static Set<String> nameSet1 = new HashSet<String>();
	private static Set<String> nameSet2 = new HashSet<String>();
	private static Set<NodeReader> nodeSet1 = new HashSet<NodeReader>();
	private static Set<NodeReader> nodeSet2 = new HashSet<NodeReader>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Create reference data generator
		System.setProperty("it.polito.dp2.NFFG.NffgVerifierFactory", "it.polito.dp2.NFFG.Random.NffgVerifierFactoryImpl");
		referenceNffgVerifier = NffgVerifierFactory.newInstance().newNffgVerifier();

		// set referenceNFFG
		if(referenceNffgVerifier.getNffgs().size()!=0){
			TreeSet<NffgReader> rts = new TreeSet<NffgReader>(new NamedEntityReaderComparator());
			rts.addAll(referenceNffgVerifier.getNffgs());
			Iterator<NffgReader> iter = rts.iterator();
			boolean found=false;
			// look for nffg with at least 4 nodes
			while(iter.hasNext() && !found) {
				referenceNFFG = iter.next();
				Set<NodeReader> nodes = referenceNFFG.getNodes();
				if (nodes.size()>3) {
					// prepare two empty sets of strings
					// create a TreeSet including the nodes of the reference NFFG
					TreeSet<NodeReader> nts = new TreeSet<NodeReader>(new NamedEntityReaderComparator());
					nts.addAll(referenceNFFG.getNodes());
					// add the first two nodes of the treeset to set 1 and the other ones to set 2
					int count=0;
					for (NodeReader nr:nts) {
						String name = nr.getName();
						if (count<2) {
							nameSet1.add(name);
							nodeSet1.add(nr);
						} else {
							nameSet2.add(name);
							nodeSet2.add(nr);
						}
						count++;
					}
					found=true;
				}
			}
			assertEquals("Tests cannot run. Please choose another seed.",found,true);
		}
		
		// read testcase property
		Long testcaseObj = Long.getLong("it.polito.dp2.NFFG.Random.testcase");
		if (testcaseObj == null)
			testcase = 0;
		else
			testcase = testcaseObj.longValue();

	}

	@Before
	public void setUp() throws Exception {
        assertNotNull("Internal tester error during test setup: null nffgverifier reference", referenceNffgVerifier);
        assertNotNull("Internal tester error during test setup: null reference NFFG", referenceNFFG);
	}

	private void createClient() throws NFFGClientException {
		// Create client under test
		try {
			testNFFGClient = NFFGClient0Factory.newInstance().newNFFGClient0();
		} catch (FactoryConfigurationError fce) {
			fce.printStackTrace();
		}
		assertNotNull("The implementation under test generated a null NFFGClient0", testNFFGClient);
	}

	@Test
	public final void testBasicLoadUnloadIsolationPolicy() {
		System.out.println("DEBUG: starting testBasicLoadUnloadIsolationPolicy");
			int rightPolicyNumber = referenceNffgVerifier.getPolicies().size(); 
		try {
			
				// 1. Load a non-existing isolation policy and check that the total number of policies has been increased by 1
				loadIsolationPolicy();	
				// Now there should be one more policy
				compareTotalPolicyNumber(rightPolicyNumber+1);

				// 2. Unload the policy previously loaded and check that the total number of policies has been decreased by 1		
				testNFFGClient.unloadIsolationPolicy(referencePolicyName);
				compareTotalPolicyNumber(rightPolicyNumber);
			
		} catch (NFFGClientException 
				| UnknownNameException 
				| ServiceException 
				| FactoryConfigurationError e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		}		

	}

	private void compareTotalPolicyNumber(int expected) {
		int returned;
		WebTarget target;
		
		// read number of policies from service
        Client c = ClientBuilder.newClient();
        target = c.target("http://localhost:8080/NffgService/rest/totalNumberOfPolicies");
        try {
        	String responseMsg = target.request().get(String.class);
        	returned = Integer.valueOf(responseMsg);
        	assertEquals("Wrong number of policies returned by get", expected ,returned);
        } catch (Exception e) {
        	fail("Retrieval of total number of policies failed.");
        }	
	}

	private void loadIsolationPolicy() throws NFFGClientException, UnknownNameException, ServiceException {
		// create client under test
		createClient();

		// call load operation
		testNFFGClient.loadIsolationPolicy(referencePolicyName, referenceNFFG.getName(), true, nameSet1, nameSet2);
	}

	@Test
	public final void testLoadUnloadIsolationPolicy() {
		System.out.println("DEBUG: starting testLoadUnloadIsolationPolicy");
			int rightPolicyNumber = referenceNffgVerifier.getPolicies().size(); 
		try {
			
				// 1. Load a non-existing isolation policy and check that the total number of policies has been increased by 1
				loadIsolationPolicy();	
				// Now there should be one more policy
				comparePolicyNumber(rightPolicyNumber+1,referenceNFFG.getName());

				// 2. Unload the policy previously loaded and check that the total number of policies has been decreased by 1		
				testNFFGClient.unloadIsolationPolicy(referencePolicyName);
				comparePolicyNumber(rightPolicyNumber,referenceNFFG.getName());
			
		} catch (NFFGClientException 
				| UnknownNameException 
				| ServiceException 
				| FactoryConfigurationError 
				| NffgVerifierException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		}	

	}

	private void comparePolicyNumber(int expected, String NffgName) throws NffgVerifierException, FactoryConfigurationError {
		// create testNffgVerifier
		createTestNffgVerifier();
		
		// read policies and check their number is right
		Set<PolicyReader> tps = testNffgVerifier.getPolicies();  
		assertNotNull("Null policy set",tps);
		assertEquals("Wrong number of policies", expected ,tps.size());
	}

	private void createTestNffgVerifier() throws NffgVerifierException, FactoryConfigurationError {
		System.setProperty("it.polito.dp2.NFFG.NffgVerifierFactory", "it.polito.dp2.NFFG.sol3.client2.NffgVerifierFactory");
		testNffgVerifier = NffgVerifierFactory.newInstance().newNffgVerifier();
		assertNotNull("The implementation under test generated a null NffgVerifier", testNffgVerifier);
	}
	
	@Test
	public final void testReturnedIsolationPolicy() {
		System.out.println("DEBUG: starting testReturnedIsolationPolicy");
		try {
		
			// 1. Load a non-existing isolation policy
			loadIsolationPolicy();
			
			// create testNffgVerifier
			createTestNffgVerifier();
			
			// get policies of reference NFFG
			Set<PolicyReader> tps = testNffgVerifier.getPolicies(referenceNFFG.getName());
			assertNotNull("Null policy set",tps);
			
			int numfound = 0;
			for (PolicyReader pr:tps) {
				if (referencePolicyName.equals(pr.getName())) {
					if (pr instanceof IsolationPolicyReader) {
						IsolationPolicyReader ipr = (IsolationPolicyReader)  pr;
						compareNodeSets (nodeSet1, ipr.getFirstNodeSet());
						compareNodeSets (nodeSet2, ipr.getSecondNodeSet());
						numfound++;
					} else
						fail("Wrong type of returned policy reader");
				}		
			}
			assertEquals("Wrong number of isolation policies found",1,numfound);

		} catch (NFFGClientException 
				| UnknownNameException 
				| ServiceException 
				| FactoryConfigurationError 
				| NffgVerifierException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		}	
	}
	
	private void compareNodeSets(Set<NodeReader> rs, Set<NodeReader> ts) {
		// if one of the two calls returned null while the other didn't return null, the test fails
		if ((rs == null) && (ts != null) || (rs != null) && (ts == null)) {
		    fail("getNodes returns null when it should return non-null or vice versa");
		    return;
		}

        // if both calls returned null, there are no nodes, and the test passes
		if ((rs == null) && (ts == null)) {
		    assertTrue("There are no nodes!", true);
		    return;
		}
		
        // check that the number of nodes matches
		assertEquals("Wrong Number of nodes", rs.size(), ts.size());
		
        // create treesets of nodes, using the comparator for sorting, one for reference and one for impl. under test 
		TreeSet<NodeReader> rts = new TreeSet<NodeReader>(new NamedEntityReaderComparator());
		TreeSet<NodeReader> tts = new TreeSet<NodeReader>(new NamedEntityReaderComparator());
   
		rts.addAll(rs);
		tts.addAll(ts);
		
		Iterator<NodeReader> ri = rts.iterator();
		Iterator<NodeReader> ti = tts.iterator();

        // check that the names of all nodes match one by one
		while (ri.hasNext() && ti.hasNext()) {
			String rn = ri.next().getName();
			String tn = ti.next().getName();
			assertEquals("Node name in set does not match", rn,tn);
		}
	}

}

class NamedEntityReaderComparator implements Comparator<NamedEntityReader> {
    public int compare(NamedEntityReader f0, NamedEntityReader f1) {
    	return f0.getName().compareTo(f1.getName());
    }
}
