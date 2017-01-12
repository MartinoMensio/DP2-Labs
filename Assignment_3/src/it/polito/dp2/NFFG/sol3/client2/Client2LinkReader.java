package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the interface LinkReader. This class is an extension of
 * Client2NamedEntityReader, adding the private fields for source and
 * destination nodes.
 * 
 * @author Martino Mensio
 *
 */
public class Client2LinkReader extends Client2NamedEntityReader implements LinkReader {

	private NodeReader sourceNode;
	private NodeReader destinationNode;

	public Client2LinkReader(String name, NodeReader sourceNode, NodeReader destionationNode) {
		super(name);
		this.sourceNode = sourceNode;
		this.destinationNode = destionationNode;
	}

	@Override
	public NodeReader getDestinationNode() {
		return destinationNode;
	}

	@Override
	public NodeReader getSourceNode() {
		return sourceNode;
	}

}
