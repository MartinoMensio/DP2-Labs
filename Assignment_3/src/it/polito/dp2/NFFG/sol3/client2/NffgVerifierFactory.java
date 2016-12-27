package it.polito.dp2.NFFG.sol3.client2;

import java.net.*;

import it.polito.dp2.NFFG.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class NffgVerifierFactory extends it.polito.dp2.NFFG.NffgVerifierFactory {

	@Override
	public NffgVerifier newNffgVerifier() throws NffgVerifierException {
		// TODO Auto-generated method stub
		//return new Client2NffgVerifier();
		try {
			String url = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
			if (url == null) {
				url = "http://localhost:8080/NffgService/rest/";
			}
			URI uri = new URL(url).toURI();
			return new Client2NffgVerifier(uri);
		} catch (URISyntaxException e) {
			// TODO: Auto-generated catch block
			throw new NffgVerifierException("Invalid URI: " + e.getMessage());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			throw new NffgVerifierException("Invalid URL: " + e.getMessage());
		}
	}

}
