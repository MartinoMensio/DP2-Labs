package it.polito.dp2.NFFG.sol3.client1;

import java.util.function.Function;
import java.util.stream.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

public class NffgReaderToJAXB implements Function<NffgReader, Nffg> {

	ObjectFactory factory = new ObjectFactory();
	
	@Override
	public Nffg apply(NffgReader nffgR) {
		Nffg result = factory.createNffg();
		result.setName(nffgR.getName());
		result.setUpdated(Utils.XMLGregorianCalendarFromCalendar(nffgR.getUpdateTime()));
		// TODO
		result.getNode().addAll(nffgR.getNodes().stream().map(n -> transformNode(n)).collect(Collectors.toList()));
		result.getLink().addAll(nffgR.getNodes().stream().flatMap(n -> n.getLinks().stream()).map(l -> transformLink(l)).collect(Collectors.toList()));
		return result;
	}

	Node transformNode(NodeReader nodeR) {
		Node result = factory.createNode();
		result.setName(nodeR.getName());
		result.setFunctionality(FunctionalityT.fromValue(nodeR.getFuncType().value()));
		return result;
	}
	
	Link transformLink(LinkReader linkR) {
		Link result = factory.createLink();
		result.setName(linkR.getName());
		NodeRefT src = factory.createNodeRefT();
		src.setRef(linkR.getSourceNode().getName());
		result.setSrc(src);
		NodeRefT dst = factory.createNodeRefT();
		dst.setRef(linkR.getDestinationNode().getName());
		result.setDst(dst);
		return result;
	}
}
