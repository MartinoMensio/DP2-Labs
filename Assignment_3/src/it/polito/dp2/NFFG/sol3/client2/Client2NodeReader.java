package it.polito.dp2.NFFG.sol3.client2;

import java.util.*;
import java.util.stream.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2NodeReader extends Client2NamedEntityReader implements NodeReader {

	private String nffgName;
	private WebTarget target;
	
	Client2NodeReader(Client2NffgReader nffgR, String name) {
		super(name);
		target = nffgR.getTarget();
		nffgName = nffgR.getName();
	}
	
	WebTarget getTarget() {
		return target;
	}
	
	String getNffgName() {
		return nffgName;
	}

	@Override
	public FunctionalType getFuncType() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/nodes/{nodeName} to have fresh data
		// return node.funcType
		NodeT node = target.path("nffgs").path(nffgName).path("nodes").path(getName()).request(MediaType.APPLICATION_JSON).get(NodeT.class);
		return FunctionalType.fromValue(node.getFunctionality().value());
	}

	@Override
	public Set<LinkReader> getLinks() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/nodes/{nodeName}/links
		List<LinkT> links = target.path("nffgs").path(nffgName).path("nodes").path(getName()).path("links").request(MediaType.APPLICATION_JSON).get(new GenericType<List<LinkT>>() {});
		return links.stream().map(l -> new Client2LinkReader(this, l.getName())).collect(Collectors.toSet());
	}

}
