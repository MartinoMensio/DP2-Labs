package it.polito.dp2.NFFG.sol3.client2;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.NodeT;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2LinkReader extends Client2NamedEntityReader implements LinkReader {

	private Client2NffgReader nffgReader;
	private WebTarget target;
	
	Client2LinkReader(Client2NffgReader nffgReader, String name) {
		super(name);
		// TODO Auto-generated constructor stub
		target = nffgReader.getVerifier().getTarget();
		this.nffgReader = nffgReader;
	}

	@Override
	public NodeReader getDestinationNode() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/links/{linkName}/dst
		NodeT dst = target.path("nffgs").path(nffgReader.getName()).path("links").path(getName()).path("dst").request(MediaType.APPLICATION_JSON).get(NodeT.class);
		// TODO fix null
		return new Client2NodeReader(nffgReader, dst.getName());
	}

	@Override
	public NodeReader getSourceNode() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/links/{linkName}/src
		NodeT src = target.path("nffgs").path(nffgReader.getName()).path("links").path(getName()).path("src").request(MediaType.APPLICATION_JSON).get(NodeT.class);
		// TODO fix null
		return new Client2NodeReader(nffgReader, src.getName());
	}

}
