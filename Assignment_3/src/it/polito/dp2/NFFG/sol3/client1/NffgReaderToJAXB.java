package it.polito.dp2.NFFG.sol3.client1;

import java.util.function.Function;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

public class NffgReaderToJAXB implements Function<NffgReader, NffgT> {

	ObjectFactory factory = new ObjectFactory();
	
	@Override
	public NffgT apply(NffgReader nffgR) {
		NffgT result = factory.createNffgT();
		result.setName(nffgR.getName());
		result.setUpdated(Utils.XMLGregorianCalendarFromCalendar(nffgR.getUpdateTime()));
		// TODO
		//result.getNode().addAll(nffgR.getNodes().parallelStream().map())
		return result;
	}

}
