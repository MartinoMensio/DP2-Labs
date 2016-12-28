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
public class Client2NffgReader extends Client2NamedEntityReader implements NffgReader {
	
	private Client2NffgVerifier verifier;
	private WebTarget target;
	private ObjectFactory factory;

	Client2NffgReader(Client2NffgVerifier verifier, String name) {
		super(name);
		this.verifier = verifier;
		target = verifier.getTarget();
		factory = new ObjectFactory();
	}
	
	Client2NffgVerifier getVerifier() {
		return verifier;
	}

	@Override
	public NodeReader getNode(String nodeName) {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/nodes/{nodeName}
		return null;
	}

	@Override
	public Set<NodeReader> getNodes() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/nodes
		List<NodeT> nodes = target.path("nffgs").path(getName()).path("nodes").request(MediaType.APPLICATION_JSON).get(new GenericType<List<NodeT>>() {});
		
		return nodes.stream().map(n -> new Client2NodeReader(this, n.getName())).collect(Collectors.toSet());
	}

	@Override
	public Calendar getUpdateTime() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName} to have fresh data
		// return nffg.updateTime
		return null;
	}

}
