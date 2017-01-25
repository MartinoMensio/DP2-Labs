package it.polito.dp2.NFFG.sol2;

import java.net.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.*;

/**
 * This is a factory class that extends
 * it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory The URL is checked by using
 * the URI class, and is then passed to the constructor of the
 * {@link ReachabilityTesterImpl} class together with the NffgVerifier data
 * 
 * @author Martino Mensio
 *
 */
public class ReachabilityTesterFactory extends it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory {

	@Override
	public ReachabilityTester newReachabilityTester() throws ReachabilityTesterException {
		it.polito.dp2.NFFG.NffgVerifierFactory factory = it.polito.dp2.NFFG.NffgVerifierFactory.newInstance();
		try {
			URI uri = new URI(System.getProperty("it.polito.dp2.NFFG.lab2.URL"));
			return new ReachabilityTesterImpl(factory.newNffgVerifier(), uri);
		} catch (NullPointerException e) {
			throw new ReachabilityTesterException("The system property is not set");
		} catch (URISyntaxException e) {
			throw new ReachabilityTesterException("Invalid URI: " + e.getMessage());
		} catch (NffgVerifierException e) {
			throw new ReachabilityTesterException("Impossible to generate data: " + e.getMessage());
		}
	}

}
