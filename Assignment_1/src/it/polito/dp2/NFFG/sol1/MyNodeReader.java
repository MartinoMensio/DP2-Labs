package it.polito.dp2.NFFG.sol1;

import java.util.*;

import it.polito.dp2.NFFG.*;

public class MyNodeReader extends MyNamedEntityReader implements NodeReader {

	private FunctionalType functionality;
	private Set<LinkReader> outgoingLinks;
	
	public MyNodeReader(String name, FunctionalType functionality) {
		super(name);
		this.functionality = functionality;
		outgoingLinks = new HashSet<>();
	}
	
	void addOutgoingLink(LinkReader link) {
		outgoingLinks.add(link);
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
