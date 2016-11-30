package it.polito.dp2.NFFG.sol2;

import java.net.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.*;

/**
 * This is a factory class that extends
 * it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory The URL is checked by using
 * the URI and URL classes, and is then passed to the constructor of the
 * {@link MyReachabilityTester} class together with the NffgVerifier data
 * 
 * @author Martino Mensio
 *
 */
public class ReachabilityTesterFactory extends it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory {

	@Override
	public ReachabilityTester newReachabilityTester() throws ReachabilityTesterException {
		it.polito.dp2.NFFG.NffgVerifierFactory factory = it.polito.dp2.NFFG.NffgVerifierFactory.newInstance();
		try {
			URL url = new URL(System.getProperty("it.polito.dp2.NFFG.lab2.URL"));
			return new MyReachabilityTester(factory.newNffgVerifier(), url.toURI());
		} catch (URISyntaxException e) {
			// TODO: Auto-generated catch block
			throw new ReachabilityTesterException("Invalid URI: " + e.getMessage());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			throw new ReachabilityTesterException("Invalid URL: " + e.getMessage());
		} catch (NffgVerifierException e) {
			// TODO Auto-generated catch block
			throw new ReachabilityTesterException("Impossible to generate data" + e.getMessage());
		}
	}

}
