package it.polito.dp2.NFFG.sol3.client2;

import java.util.*;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2NodeReader extends Client2NamedEntityReader implements NodeReader {

	private String nffgName;
	
	Client2NodeReader(Client2NffgReader client2NffgReader, String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public FunctionalType getFuncType() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/nodes/{nodeName} to have fresh data
		// return node.funcType
		return null;
	}

	@Override
	public Set<LinkReader> getLinks() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/nodes/{nodeName}/links
		return null;
	}

}
