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
		try {
			String url = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
			if (url == null) {
				url = "http://localhost:8080/NffgService/rest/";
			}
			URI uri = new URI(url);
			return new NffgVerifierDataProvider(uri).getNffgVerifierData();
		} catch (URISyntaxException e) {
			throw new NffgVerifierException("Invalid URI: " + e.getMessage());
		} catch (NffgVerifierException e) {
			throw e;
		} catch (Exception e) {
			// last chance for all the runtime exceptions
			throw new NffgVerifierException(e.getMessage());
		}
	}

}
