package it.polito.dp2.NFFG.sol1;

import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NodeReader;

public class MyNodeReader extends MyNamedEntityReader implements NodeReader {

	public MyNodeReader(String name) {
		// TODO Auto-generated constructor stub
		super(name);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FunctionalType getFuncType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<LinkReader> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

}
