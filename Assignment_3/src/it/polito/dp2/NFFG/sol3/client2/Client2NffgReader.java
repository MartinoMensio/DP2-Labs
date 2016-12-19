package it.polito.dp2.NFFG.sol3.client2;

import java.util.*;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2NffgReader extends Client2NamedEntityReader implements NffgReader {

	Client2NffgReader(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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
		return null;
	}

	@Override
	public Calendar getUpdateTime() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName} to have fresh data
		// return nffg.updateTime
		return null;
	}

}
