package it.polito.dp2.NFFG.sol3.client2.library;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the interface LinkReader. This class is an extension of
 * NamedEntityReaderImpl, adding the private fields for source and destination
 * nodes.
 * 
 * @author Martino Mensio
 *
 */
public class LinkReaderImpl extends NamedEntityReaderImpl implements LinkReader {

	private NodeReader sourceNode;
	private NodeReader destinationNode;

	public LinkReaderImpl(String name, NodeReader sourceNode, NodeReader destionationNode) {
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
