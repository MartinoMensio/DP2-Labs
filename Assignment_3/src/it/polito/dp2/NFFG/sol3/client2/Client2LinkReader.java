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

	private String nffgName;
	private WebTarget target;
	
	Client2LinkReader(Client2NodeReader client2NodeReader, String name) {
		super(name);
		// TODO Auto-generated constructor stub
		target = client2NodeReader.getTarget();
		nffgName = client2NodeReader.getNffgName();
	}

	@Override
	public NodeReader getDestinationNode() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/links/{linkName}/dst
		NodeT dst = target.path("nffgs").path(nffgName).path("links").path(getName()).path("dst").request(MediaType.APPLICATION_JSON).get(NodeT.class);
		// TODO fix null
		return new Client2NodeReader(null, dst.getName());
	}

	@Override
	public NodeReader getSourceNode() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/links/{linkName}/src
		NodeT src = target.path("nffgs").path(nffgName).path("links").path(getName()).path("src").request(MediaType.APPLICATION_JSON).get(NodeT.class);
		// TODO fix null
		return new Client2NodeReader(null, src.getName());
	}

}
