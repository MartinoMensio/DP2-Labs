package it.polito.dp2.NFFG.sol3.client1;

import java.net.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.lab3.test0.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class NFFGClient0Factory extends it.polito.dp2.NFFG.lab3.test0.NFFGClient0Factory {

	@Override
	public NFFGClient0 newNFFGClient0() throws NFFGClientException {
		it.polito.dp2.NFFG.NffgVerifierFactory factory = it.polito.dp2.NFFG.NffgVerifierFactory.newInstance();
		try {
			String url = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
			if (url == null) {
				url = "http://localhost:8080/NffgService/rest/";
			}
			URI uri = new URI(url);
			return new NFFGClient0Impl(factory.newNffgVerifier(), uri);
		} catch (URISyntaxException e) {
			throw new NFFGClientException("Invalid URI: " + e.getMessage());
		} catch (NffgVerifierException e) {
			throw new NFFGClientException("Impossible to generate data" + e.getMessage());
		}
	}

}
