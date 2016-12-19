package it.polito.dp2.NFFG.sol3.client1;

import java.net.URI;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.jaxb.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client1NFFGClient implements NFFGClient {

	private NffgVerifier verifier;
	private WebTarget target;
	private NffgReaderToJAXB nffgReaderTransformer;

	public Client1NFFGClient(NffgVerifier verifier, URI uri) {
		this.verifier = verifier;
		target = ClientBuilder.newClient().target(uri);
		nffgReaderTransformer = new NffgReaderToJAXB();
	}

	@Override
	public void loadNFFG(String name) throws UnknownNameException, AlreadyLoadedException, ServiceException {
		// get the corresponding NffgReader
		NffgReader nffgR = verifier.getNffg(name);
		if (nffgR == null) {
			throw new UnknownNameException("No NFFG with name " + name);
		}
		// call transformer from NffgReader to NffgT
		NffgT nffg = nffgReaderTransformer.apply(nffgR);
		// POST /nffgs
		// TODO move this request to a specific method, so that also loadAll()
		// can call it. In this way a centralized management of exceptions will
		// be applied
		try {
			Response res = target.path("nffgs").request(MediaType.APPLICATION_XML)
					.post(Entity.entity(nffg, MediaType.APPLICATION_XML));
			if (res.getStatus() == 403) {
				// if already loaded throw AlreadyLoadedException
				throw new AlreadyLoadedException("nffg with name " + nffg.getName() + "already exists");
			}
		} catch (Exception e) {
			// TODO: handle exception
			// if other errors throw ServiceException
			throw new ServiceException("something bad happened: " + e.getMessage());
		}
	}

	@Override
	public void loadAll() throws AlreadyLoadedException, ServiceException {
		// TODO Auto-generated method stub
		// foreach nffg (from verifier): POST /nffgs
		for (NffgReader nffgR : verifier.getNffgs()) {
			try {
				loadNFFG(nffgR.getName());
			} catch (UnknownNameException e) {
				// TODO Auto-generated catch block
				System.err.println("this is impossible");
				System.exit(1);
			}
		}
		// TODO
		// foreach policy (from verifier): POST /nffgs/{nffgName}/policies
	}

	@Override
	public void loadReachabilityPolicy(String name, String nffgName, boolean isPositive, String srcNodeName,
			String dstNodeName) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		// build a PolicyT from the values
		// if nffgName or srcNodeName or dstNodeName don't correspond to local info, throw UnknownNameException
		// POST /nffgs/{nffgName}/policies
		// if some errors with communication with server, throw ServiceException

	}

	@Override
	public void unloadReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		// DELETE /policies/{policyName}
		// if 404 throw UnknownNameException
		// if other errors throw ServiceException
	}

	@Override
	public boolean testReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		// GET /policies/{policyName}/result
		// if 404 throw UnknownNameException
		// if other errors throw ServiceException
		// return result.verificationResult
		return false;
	}

}
