package it.polito.dp2.NFFG.sol2;

import java.net.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.*;

public class MyReachabilityTester implements ReachabilityTester {

	private NffgVerifier monitor;
	private URI uri;
	private String graphName;

	public MyReachabilityTester(NffgVerifier nffgR, URI uri) {
		this.monitor = nffgR;
		this.uri = uri;
		graphName = null;
	}

	@Override
	public void loadNFFG(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		NffgReader nffgR = monitor.getNffg(name);
		if (nffgR == null) {
			throw new UnknownNameException("no nffg with name " + name);
		}
		// clean the graphName (for failures)
		graphName = null;

		// TODO delete the previous graph

		// TODO upload the new NFFG

		// on success, overwrite the graphName
		graphName = name;
	}

	@Override
	public boolean testReachability(String srcName, String destName)
			throws UnknownNameException, ServiceException, NoGraphException {
		if (graphName == null) {
			throw new NoGraphException("no graph is currently loaded, please call loadNFFG");
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCurrentGraphName() {
		return graphName;
	}

}
