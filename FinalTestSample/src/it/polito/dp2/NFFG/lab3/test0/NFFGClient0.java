package it.polito.dp2.NFFG.lab3.test0;

import java.util.Set;

import it.polito.dp2.NFFG.lab3.NFFGClient;
import it.polito.dp2.NFFG.lab3.ServiceException;
import it.polito.dp2.NFFG.lab3.UnknownNameException;

public interface NFFGClient0 extends NFFGClient {
	/**
	 * Loads a new isolation policy into the remote service given the policy properties.
	 * If a policy with the given name already exists in the service, the new policy substitutes
	 * the old one. The new policy is uploaded without a verification result.
	 * @param name	the name to be given to the new policy
	 * @param nffgName	the name of a known NFFG the new policy refers to
	 * @param isPositive	true if the new policy is positive
	 * @param nodeSet1	the set of node names corresponding to the first set of nodes
	 * @param nodeSet2	the set of node names corresponding to the second set of nodes
	 * @throws UnknownNameException	if nffgName is not the name of a known NFFG, or nodeSet1 or nodeSet2 include names that do not correspond to nodes belonging to the known NFFG named nffgName.
	 * @throws ServiceException	if any other error occurs when trying to upload the policy.
	 */
	void loadIsolationPolicy(String name, String nffgName, boolean isPositive, Set<String> nodeSet1, Set<String> nodeSet2) throws UnknownNameException, ServiceException;

	/**
	 * Unloads the isolation policy with the given name from the remote service
	 * @param name	the name of the isolation policy to be unloaded
	 * @throws UnknownNameException if the policy name passed as argument does not correspond to a isolation policy already loaded in the remote service
	 * @throws ServiceException	if any other error occurs when trying to unload the policy
	 */
	void unloadIsolationPolicy(String name) throws UnknownNameException, ServiceException;
	
}
