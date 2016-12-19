package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client2NamedEntityReader implements NamedEntityReader {

	private String name;
	
	Client2NamedEntityReader(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

}
