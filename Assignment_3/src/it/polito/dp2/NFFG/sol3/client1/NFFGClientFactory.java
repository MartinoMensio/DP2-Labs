package it.polito.dp2.NFFG.sol3.client1;

import java.net.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab3.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class NFFGClientFactory extends it.polito.dp2.NFFG.lab3.NFFGClientFactory {

	@Override
	public NFFGClient newNFFGClient() throws NFFGClientException {
		it.polito.dp2.NFFG.NffgVerifierFactory factory = it.polito.dp2.NFFG.NffgVerifierFactory.newInstance();
		try {
			String url = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
			if (url == null) {
				url = "http://localhost:8080/NffgService/rest/";
			}
			URI uri = new URI(url);
			return new Client1NFFGClient(factory.newNffgVerifier(), uri);
		} catch (URISyntaxException e) {
			throw new NFFGClientException("Invalid URI: " + e.getMessage());
		} catch (NffgVerifierException e) {
			throw new NFFGClientException("Impossible to generate data" + e.getMessage());
		}
	}

}
