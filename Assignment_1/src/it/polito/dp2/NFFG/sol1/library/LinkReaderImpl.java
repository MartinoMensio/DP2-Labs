package it.polito.dp2.NFFG.sol1.library;

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

	public LinkReaderImpl(String name, NodeReader sourceNode, NodeReader destinationNode) throws NffgVerifierException {
		super(name);
		if (sourceNode == null || destinationNode == null) {
			throw new NffgVerifierException("something null when creating a LinkReader");
		}
		this.sourceNode = sourceNode;
		this.destinationNode = destinationNode;
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
