package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.NamedEntityReader;

public class MyNamedEntityReader implements NamedEntityReader {

	private String name;
	
	public MyNamedEntityReader(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
