package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2LinkReader extends Client2NamedEntityReader implements LinkReader {

	private String nffgName;
	
	Client2LinkReader(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public NodeReader getDestinationNode() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/links/{linkName}/dst
		return null;
	}

	@Override
	public NodeReader getSourceNode() {
		// TODO Auto-generated method stub
		// GET /nffgs/{nffgName}/links/{linkName}/src
		return null;
	}

}
