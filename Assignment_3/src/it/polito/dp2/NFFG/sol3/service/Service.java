package it.polito.dp2.NFFG.sol3.service;

import java.util.*;

import it.polito.dp2.NFFG.sol3.jaxb.*;

/**
 * Core functionality: orchestrator + response builder. Called by the web
 * interface, this class interacts with the persistence and the Neo4JXML client
 * 
 * @author Martino Mensio
 *
 */
public class Service {

	// retrieve data from persistence
	private Persistence data = Persistence.getPersistence();

}
