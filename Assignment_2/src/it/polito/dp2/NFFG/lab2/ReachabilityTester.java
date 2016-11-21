package it.polito.dp2.NFFG.lab2;

/**
 * An interface for interacting with a remote service that
 * provides the possibility to upload a graph and test
 * reachability in the loaded graph.
 *
 */
public interface ReachabilityTester {
	
	/**
	 * Loads the NFFG with the given name into the remote service.
	 * Any previously loaded graph has to be overwritten by this one.
	 * @param name	the name of the NFFG to be loaded
	 * @throws UnknownNameException	if the name passed as argument does not correspond to a known NFFG. No alteration of data on the server occurs in this case.
	 * @throws ServiceException	if any other error occurs when trying to upload the NFFG. The load operation may have been executed partially in this case.
	 */
	void loadNFFG(String name) throws UnknownNameException, ServiceException;
	
	/**
	 * Tests reachability from a source node to a destination node in the previously uploaded
	 * graph by means of the remote service.
	 * @param srcName	the name of the source node
	 * @param destName	the name of the destination node
	 * @return	true if the destination node is reachable from the source node, false otherwise
	 * @throws UnknownNameException if at least one of the names passed as arguments does not correspond to a node existing in the loaded graph
	 * @throws NoGraphException	if no graph is currently loaded
	 * @throws ServiceException	if any other error occurs when trying to test reachability
	 */
	boolean testReachability(String srcName, String destName) throws UnknownNameException, ServiceException, NoGraphException;
	
	/**
	 * Gets the name of the currently loaded graph
	 * @return	the name of the currently loaded graph or null if no graph is currently loaded (includes the case of failure of the last attempt to load a graph).
	 * This is a local operation that cannot fail.
	 */
	String getCurrentGraphName();
}
