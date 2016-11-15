package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.*;

public class MyLinkReader extends MyNamedEntityReader implements LinkReader {

	private NodeReader sourceNode;
	private NodeReader destinationNode;

	public MyLinkReader(String name, NodeReader sourceNode, NodeReader destionationNode) {
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
