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

	private boolean incomplete;
	private boolean ok;

	public NffgStorage(Nffg nffg, Map<String, String> idMappings) {
		this.nffg = nffg;
		this.idMappings = idMappings;

		this.incomplete = true;
	}

	public synchronized void setOK() {
		this.incomplete = false;
		this.ok = true;
		this.notifyAll();
	}

	public synchronized void setKO() {
		this.incomplete = false;
		this.ok = false;
		this.notifyAll();
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
		synchronized (this) {
			while (incomplete) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return null;
				}
			}
		}
		return ok ? idMappings.get(nodeName) : null;
	}
}
