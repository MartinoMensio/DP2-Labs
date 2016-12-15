package it.polito.dp2.NFFG.sol3.service;

import java.util.*;

import it.polito.dp2.NFFG.sol3.jaxb.*;

/**
 * Class for persistence
 * 
 * @author Martino Mensio
 *
 */
public class Persistence {
	// singleton class
	private static Persistence data = new Persistence();

	// instance data

	// policies are stored in the service
	public Map<String, PolicyT> policiesMap = new HashMap<>();
	// the service only memorizes the mappings between node name and node id
	public Map<String, String> nodesId = new HashMap<>();

	private Persistence() {

	}

	public static Persistence getPersistence() {
		return data;
	}

}
