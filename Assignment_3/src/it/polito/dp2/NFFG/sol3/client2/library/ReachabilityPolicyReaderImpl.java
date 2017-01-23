package it.polito.dp2.NFFG.sol3.client2.library;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the ReachabilityPolicyReader
 * 
 * @author Martino Mensio
 *
 */
public class ReachabilityPolicyReaderImpl extends PolicyReaderImpl implements ReachabilityPolicyReader {

	private NodeReader src;
	private NodeReader dst;

	public ReachabilityPolicyReaderImpl(String name, NffgReader nffg, VerificationResultReader result,
			Boolean expected, NodeReader src, NodeReader dst) {
		super(name, nffg, result, expected);
		this.src = src;
		this.dst = dst;
	}

	@Override
	public NodeReader getDestinationNode() {
		return src;
	}

	@Override
	public NodeReader getSourceNode() {
		return dst;
	}

}