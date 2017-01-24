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
		nffg.getNode().forEach(n -> {
			try {
				nffgR.addNode(transformNode(n));
			} catch (NffgVerifierException e) {
				// to go through lambda must use an unchecked exception
				throw new RuntimeException(e);
			}
		});
		// and process links
		nffg.getLink().forEach(l -> {
			// get references to the source and destination nodes.
			// The source must be later modified, so need to use the getSol1Node
			// method
			NodeReaderImpl src = nffgR.getNodeReaderImpl(l.getSrc().getRef());
			NodeReader dst = nffgR.getNode(l.getDst().getRef());
			// build the link
			LinkReader linkR = new LinkReaderImpl(l.getName(), src, dst);
			// add the circular reference to the getSol1Node
			try {
				src.addOutgoingLink(linkR);
			} catch (NffgVerifierException e) {
				// to go through lambda must use an unchecked exception
				throw new RuntimeException(e);
			}
		});

		return nffgR;
	}

	private NodeReader transformNode(Node node) {
		return new NodeReaderImpl(node.getName(), FunctionalType.fromValue(node.getFunctionality().value()));
	}

}
