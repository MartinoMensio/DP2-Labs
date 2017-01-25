package it.polito.dp2.NFFG.sol3.client1;

import java.util.function.Function;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * This class takes a NffgReader object and returns a corresponding object
 * belonging to the generated class Nffg
 * 
 * @author Martino Mensio
 *
 */
public class NffgReaderToJaxb implements Function<NffgReader, Nffg> {

	ObjectFactory factory = new ObjectFactory();

	@Override
	public Nffg apply(NffgReader nffgR) {
		Nffg result = factory.createNffg();
		result.setName(nffgR.getName());
		result.setUpdated(Utils.XMLGregorianCalendarFromCalendar(nffgR.getUpdateTime()));
		// add all the nodes
		result.getNode().addAll(nffgR.getNodes().stream().map(n -> transformNode(n)).collect(Collectors.toList()));
		// add all the links
		result.getLink().addAll(nffgR.getNodes().stream().flatMap(n -> n.getLinks().stream()).map(l -> transformLink(l))
				.collect(Collectors.toList()));
		return result;
	}

	private Node transformNode(NodeReader nodeR) {
		Node result = factory.createNode();
		result.setName(nodeR.getName());
		result.setFunctionality(Functionality.fromValue(nodeR.getFuncType().value()));
		return result;
	}

	private Link transformLink(LinkReader linkR) {
		Link result = factory.createLink();
		result.setName(linkR.getName());
		NodeRef src = factory.createNodeRef();
		src.setRef(linkR.getSourceNode().getName());
		result.setSrc(src);
		NodeRef dst = factory.createNodeRef();
		dst.setRef(linkR.getDestinationNode().getName());
		result.setDst(dst);
		return result;
	}
}
