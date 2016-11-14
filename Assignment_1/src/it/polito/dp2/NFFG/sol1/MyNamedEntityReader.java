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

	/**
	 * @param other
	 *            the other Object to compare with
	 * @return if the object is equal to the other
	 * 
	 *         This metod compares NamedEntityReader objects; the comparison is
	 *         based on the name
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof NamedEntityReader) {
			return name.equals(((NamedEntityReader) other).getName());
		}
		return false;
	}

}
