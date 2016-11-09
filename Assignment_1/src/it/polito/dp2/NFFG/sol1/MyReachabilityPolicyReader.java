package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.ReachabilityPolicyReader;

public class MyReachabilityPolicyReader extends MyPolicyReader implements ReachabilityPolicyReader {

	public MyReachabilityPolicyReader(String name) {
		// TODO Auto-generated constructor stub
		super(name);
	}

	@Override
	public NodeReader getDestinationNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeReader getSourceNode() {
		// TODO Auto-generated method stub
		return null;
	}

}
