/**
 * 
 */
package it.polito.dp2.NFFG.lab3.test0;

import it.polito.dp2.NFFG.FactoryConfigurationError;
import it.polito.dp2.NFFG.lab3.NFFGClientException;

/**
 * Defines a factory API that enables applications to obtain one or more objects
 * implementing the {@link NFFGClient} interface.
 *
 */
public abstract class NFFGClient0Factory {

	private static final String propertyName = "it.polito.dp2.NFFG.NFFGClient0Factory";
	
	protected NFFGClient0Factory() {}
	
	/**
	 * Obtain a new instance of a <tt>NFFGClient0Factory</tt>.
	 * 
	 * <p>
	 * This static method creates a new factory instance. This method uses the
	 * <tt>it.polito.dp2.NFFG.NFFGClient0Factory</tt> system property to
	 * determine the NFFGClient0Factory implementation class to load.
	 * </p>
	 * <p>
	 * Once an application has obtained a reference to a
	 * <tt>NFFGClient0Factory</tt> it can use the factory to obtain a new
	 * {@link NFFGClient0} instance.
	 * </p>
	 * 
	 * @return a new instance of a <tt>NFFGClient0Factory</tt>.
	 * 
	 * @throws FactoryConfigurationError if the implementation is not available 
	 * or cannot be instantiated.
	 */
	public static NFFGClient0Factory newInstance() throws FactoryConfigurationError {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		if(loader == null) {
			loader = NFFGClient0Factory.class.getClassLoader();
		}
		
		String className = System.getProperty(propertyName);
		if (className == null) {
			throw new FactoryConfigurationError("cannot create a new instance of a NFFGClientFactory"
												+ "since the system property '" + propertyName + "'"
												+ "is not defined");
		}
		
		try {
			Class<?> c = (loader != null) ? loader.loadClass(className) : Class.forName(className);
			return (NFFGClient0Factory) c.newInstance();
		} catch (Exception e) {
			throw new FactoryConfigurationError(e, "error instantiatig class '" + className + "'.");
		}
	}
	
	
	/**
	 * Creates a new instance of a {@link NFFGClient0} implementation.
	 * 
	 * @return a new instance of a {@link NFFGClient0} implementation.
	 * @throws NFFGClientException if an implementation of {@link NFFGClient0} cannot be created.
	 */
	public abstract NFFGClient0 newNFFGClient0() throws NFFGClientException;
}