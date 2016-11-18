package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.*;

/**
 * Implementation of the interface NamedEntityReader. This class simply contains
 * a name. The equals method overrides the Object::equals and therefore enables
 * the comparison based on the name, that in the assignment is always used as a
 * key.
 * 
 * @author Martino Mensio
 *
 */
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
	 * This method compares NamedEntityReader objects. The comparison is based
	 * on the name
	 * 
	 * @param other
	 *            the other Object to compare with
	 * @return if the object is equal to the other
	 * 
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof NamedEntityReader) {
			return name.equals(((NamedEntityReader) other).getName());
		}
		return false;
	}

}
