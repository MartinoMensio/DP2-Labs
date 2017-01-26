package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.concurrent.*;

import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * Class for data storage. This is not a singleton class and has no static data
 * or methods. The caller (the Service) creates a single instance of this class
 * when it is created, and the Service class itself is singleton. This choice
 * has been done in order to easily allow the service to have multiple
 * "databases" whose lifecycle could be managed explicitly.
 * 
 * @author Martino Mensio
 *
 */
public class DataStorage {

	// NFFGs are cached in the service together with mappings between node name
	// and node id
	private final ConcurrentMap<String, NffgStorage> nffgsMap;
	// policies are stored in the service
	private final ConcurrentMap<String, Policy> policiesMap;

	protected DataStorage() {
		nffgsMap = new ConcurrentSkipListMap<>();
		policiesMap = new ConcurrentSkipListMap<>();
	}

	public ConcurrentMap<String, NffgStorage> getNffgsMap() {
		return nffgsMap;
	}

	public ConcurrentMap<String, Policy> getPoliciesMap() {
		return policiesMap;
	}

}
