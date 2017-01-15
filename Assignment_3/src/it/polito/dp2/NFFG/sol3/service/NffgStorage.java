package it.polito.dp2.NFFG.sol3.service;

import java.util.*;

import it.polito.dp2.NFFG.sol3.service.jaxb.Nffg;

/**
 * 
 * @author Martino Mensio
 *
 */
public class NffgStorage {

	// once object is created, can only read them (to solve synch issues)
	private final Nffg nffg;
	private final Map<String, String> idMappings;

	public NffgStorage(Nffg nffg, Map<String, String> idMappings) {
		this.nffg = nffg;
		this.idMappings = idMappings;
	}

	/**
	 * Read the stored Nffg object
	 * 
	 * @return the Nffg object
	 */
	public Nffg getNffg() {
		return nffg;
	}

	/**
	 * Read the neo4j id from the private map
	 * 
	 * @param nodeName
	 *            the name of the node whose id needs to be known
	 * @return the neo4j id corresponding to nodeName or null if the mapping is
	 *         not stored
	 */
	public String getId(String nodeName) {
		return idMappings.get(nodeName);
	}
}
