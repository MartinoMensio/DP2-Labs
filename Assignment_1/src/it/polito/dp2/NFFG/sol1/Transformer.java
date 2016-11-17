package it.polito.dp2.NFFG.sol1;

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
 */
public interface Transformer<T extends Object, R extends Object> {
	/**
	 * The method transform is responsible for doing the transformation.
	 * 
	 * @return
	 */
	public R transform();
}
