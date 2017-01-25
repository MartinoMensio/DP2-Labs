package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.client2.library.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

public class NffgTransformer implements ThrowingTransformer<Nffg, NffgReaderImpl, NffgVerifierException> {

	private NffgTransformer() {
	}

	public static ThrowingTransformer<Nffg, NffgReaderImpl, NffgVerifierException> newNffgTransformer() {
		return new NffgTransformer();
	}

	@Override
	public NffgReaderImpl transform(Nffg nffg) throws NffgVerifierException {
		NffgReaderImpl nffgR = new NffgReaderImpl(nffg.getName(),
				Utils.CalendarFromXMLGregorianCalendar(nffg.getUpdated()));
		// process nodes
		for (Node node : nffg.getNode()) {
			nffgR.addNode(transformNode(node));
		}
		// and process links
		for (Link link : nffg.getLink()) {
			// get references to the source and destination nodes.
			// The source must be later modified, so need to use the getSol1Node
			// method
			NodeReaderImpl src = nffgR.getNodeReaderImpl(link.getSrc().getRef());
			NodeReader dst = nffgR.getNode(link.getDst().getRef());
			// build the link
			LinkReader linkR = new LinkReaderImpl(link.getName(), src, dst);
			// add the circular reference to the getSol1Node
			src.addOutgoingLink(linkR);
		}

		return nffgR;

	}

	private NodeReader transformNode(Node node) throws NffgVerifierException {
		return new NodeReaderImpl(node.getName(), FunctionalType.fromValue(node.getFunctionality().value()));
	}

}
