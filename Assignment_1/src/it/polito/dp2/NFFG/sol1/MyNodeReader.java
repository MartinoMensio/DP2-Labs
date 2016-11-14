package it.polito.dp2.NFFG.sol1;

import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NodeReader;

public class MyNodeReader extends MyNamedEntityReader implements NodeReader {

	private FunctionalType functionality;
	private Set<LinkReader> outgoingLinks;
	
	public MyNodeReader(String name, FunctionalType functionality, Set<LinkReader> outgoingLinks) {
		// TODO Auto-generated constructor stub
		super(name);
		this.functionality = functionality;
		this.outgoingLinks = outgoingLinks;
	}

	@Override
	public FunctionalType getFuncType() {
		return functionality;
	}

	@Override
	public Set<LinkReader> getLinks() {
		return outgoingLinks;
	}

}
