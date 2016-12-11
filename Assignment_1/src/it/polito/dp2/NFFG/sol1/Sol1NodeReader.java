package it.polito.dp2.NFFG.sol1;

import java.util.*;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the NodeReader interface
 * 
 * @author Martino Mensio
 *
 */
public class Sol1NodeReader extends Sol1NamedEntityReader implements NodeReader {

	private FunctionalType functionality;
	// the Set of outgoing links
	private Map<String, LinkReader> outgoingLinks;

	public Sol1NodeReader(String name, FunctionalType functionality) {
		super(name);
		this.functionality = functionality;
		outgoingLinks = new HashMap<>();
	}

	/**
	 * Adds an outgoing link to the private Set. This method does not belong to
	 * the interface
	 * 
	 * @param link
	 * @throws NffgVerifierException
	 *             if a link with this name already exists
	 */
	void addOutgoingLink(LinkReader link) throws NffgVerifierException {
		if (outgoingLinks.containsKey(link.getName())) {
			throw new NffgVerifierException(
					"a link with the name " + link.getName() + " already exists in the nffg " + getName());
		}
		outgoingLinks.put(link.getName(), link);
	}

	@Override
	public FunctionalType getFuncType() {
		return functionality;
	}

	@Override
	public Set<LinkReader> getLinks() {
		return new HashSet<>(outgoingLinks.values());
	}

}