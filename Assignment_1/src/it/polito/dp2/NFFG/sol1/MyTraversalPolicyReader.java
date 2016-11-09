package it.polito.dp2.NFFG.sol1;

import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.TraversalPolicyReader;

public class MyTraversalPolicyReader extends MyPolicyReader implements TraversalPolicyReader {

	public MyTraversalPolicyReader(String name) {
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

	@Override
	public Set<FunctionalType> getTraversedFuctionalTypes() {
		// TODO Auto-generated method stub
		return null;
	}

}
