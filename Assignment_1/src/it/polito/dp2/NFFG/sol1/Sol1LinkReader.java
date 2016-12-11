package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the interface LinkReader. This class is an extension of
 * Sol1NamedEntityReader, adding the private fields for source and destination
 * nodes.
 * 
 * @author Martino Mensio
 *
 */
public class Sol1LinkReader extends Sol1NamedEntityReader implements LinkReader {

	private NodeReader sourceNode;
	private NodeReader destinationNode;

	public Sol1LinkReader(String name, NodeReader sourceNode, NodeReader destionationNode) {
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
