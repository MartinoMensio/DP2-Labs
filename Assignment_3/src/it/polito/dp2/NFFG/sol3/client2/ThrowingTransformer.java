package it.polito.dp2.NFFG.sol3.client2;

/**
 * This is a generic interface for performing transformation from an object
 * belonging to a class T to a class R.
 * 
 * @author Martino Mensio
 *
 * @param <T>
 *            the input class to be transformed
 * @param <R>
 *            the output class
 * @param <E>
 *            the exception that the transform method can throw (use
 *            RuntimeException in case no checked exceptions are thrown)
 */
public interface ThrowingTransformer<T extends Object, R extends Object, E extends Exception> {
	/**
	 * The method transform is responsible for doing the transformation.
	 * 
	 * @return
	 * @throws E
	 */
	public R transform(T input) throws E;
}

