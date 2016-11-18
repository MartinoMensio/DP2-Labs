package it.polito.dp2.NFFG.sol1;

import java.util.*;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the NodeReader interface
 * 
 * @author Martino Mensio
 *
 */
public class MyNodeReader extends MyNamedEntityReader implements NodeReader {

	private FunctionalType functionality;
	// the Set of outgoing links
	private Set<LinkReader> outgoingLinks;

	public MyNodeReader(String name, FunctionalType functionality) {
		super(name);
		this.functionality = functionality;
		outgoingLinks = new HashSet<>();
	}

	/**
	 * Adds an outgoing link to the private Set. This method does not belong to
	 * the interface
	 * 
	 * @param link
	 */
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
