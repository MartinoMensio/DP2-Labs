package it.polito.dp2.NFFG.sol1;

import java.util.*;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the interface NffgReader. The class is an extension of
 * MyNamedEntityReader, adding the set of nodes and the updateTime.
 * 
 * @author Martino Mensio
 *
 */
public class MyNffgReader extends MyNamedEntityReader implements NffgReader {

	// the nodes mapped by their name
	private Map<String, NodeReader> nodes;
	private Calendar updateTime;

	public MyNffgReader(String name, Calendar updateTime) {
		super(name);
		this.updateTime = updateTime;
		this.nodes = new HashMap<>();
	}

	/**
	 * Adds a node to the private collection of nodes. This is not a method of
	 * the interface.
	 * 
	 * @param node
	 */
	void addNode(NodeReader node) {
		nodes.put(node.getName(), node);
	}

	/**
	 * This method is similar to the getNode one, but it returns an object of
	 * type MyNodeReader instead of NodeReader. The visibility is only inside
	 * the package. This method allows other classes in the package to use the
	 * addOutgoingLink method. This is not a method of the interface.
	 * 
	 * @param nodeName
	 *            the name of the node to retrieve
	 * @return a MyNodeReader object with the corresponding name
	 * 
	 */
	MyNodeReader getMyNode(String nodeName) {
		NodeReader tmp = nodes.get(nodeName);
		if (tmp != null && tmp instanceof MyNodeReader) {
			return (MyNodeReader) tmp;
		}
		return null;
	}

	@Override
	public NodeReader getNode(String nodeName) {
		return nodes.get(nodeName);
	}

	@Override
	public Set<NodeReader> getNodes() {
		// transform the map into a set
		return new HashSet<>(nodes.values());
	}

	@Override
	public Calendar getUpdateTime() {
		return updateTime;
	}

}
