package it.polito.dp2.NFFG.sol3.client1;

import java.net.URI;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * 
 * @author Martino Mensio
 *
 */
public class Client1NFFGClient implements NFFGClient {

	private NffgVerifier verifier;
	private WebTarget target;
	private NffgReaderToJaxb nffgReaderTransformer;
	private PolicyReaderToJaxb policyReaderTransformer;

	public Client1NFFGClient(NffgVerifier verifier, URI uri) {
		this.verifier = verifier;
		target = ClientBuilder.newClient().target(uri);
		nffgReaderTransformer = new NffgReaderToJaxb();
		policyReaderTransformer = new PolicyReaderToJaxb();
	}

	@Override
	public void loadNFFG(String name) throws UnknownNameException, AlreadyLoadedException, ServiceException {
		// get the corresponding NffgReader
		NffgReader nffgR = verifier.getNffg(name);
		if (nffgR == null) {
			throw new UnknownNameException("no locally known NFFG corresponds to the name " + name);
		}
		loadNffg(nffgR);
	}

	@Override
	public void loadAll() throws AlreadyLoadedException, ServiceException {
		// load all the nffgs
		for (NffgReader nffgR : verifier.getNffgs()) {
			loadNffg(nffgR);
		}
		// load all the policies
		for (PolicyReader policyR : verifier.getPolicies()) {
			Policy policy = policyReaderTransformer.apply(policyR);
			loadPolicy(policy);
		}
	}

	@Override
	public void loadReachabilityPolicy(String name, String nffgName, boolean isPositive, String srcNodeName,
			String dstNodeName) throws UnknownNameException, ServiceException {
		// check the references
		NffgReader nffgR = verifier.getNffg(nffgName);
		if (nffgR == null) {
			throw new UnknownNameException("the policy refers to a locally unknown nffg " + nffgName);
		}

		NodeReader src = nffgR.getNode(srcNodeName);
		if (src == null) {
			System.err.println("src null: " + srcNodeName);
			throw new UnknownNameException(
					"the policy source node " + srcNodeName + " does not belong to the declared nffg " + nffgName);
		}
		NodeReader dst = nffgR.getNode(dstNodeName);
		if (dst == null) {
			System.err.println("dst null: " + dstNodeName);
			throw new UnknownNameException(
					"the policy destination node " + dstNodeName + "does not belong to the declared nffg " + nffgName);
		}
		// build a Policy from the values
		ObjectFactory factory = policyReaderTransformer.getFactory();
		Policy policy = factory.createPolicy();
		policy.setName(name);
		NodeRefT srcRef = factory.createNodeRefT();
		NodeRefT dstRef = factory.createNodeRefT();
		srcRef.setRef(srcNodeName);
		dstRef.setRef(dstNodeName);
		policy.setSrc(srcRef);
		policy.setDst(dstRef);
		policy.setNffg(nffgName);
		policy.setPositive(isPositive);
		// load the policy
		loadPolicy(policy);
	}

	/**
	 * POST /nffgs
	 * 
	 * @param nffgR
	 * @throws ServiceException
	 * @throws AlreadyLoadedException
	 */
	void loadNffg(NffgReader nffgR) throws ServiceException, AlreadyLoadedException {
		// call transformer from NffgReader to Nffg
		Nffg nffg = nffgReaderTransformer.apply(nffgR);
		Response res;
		try {
			res = target.path("nffgs").request(MediaType.APPLICATION_XML)
					.post(Entity.entity(nffg, MediaType.APPLICATION_XML));

		} catch (Exception e) {
			// TODO: handle exception
			// if other errors throw ServiceException
			throw new ServiceException("something bad happened: " + e.getMessage());
		}
		if (res.getStatus() == 409) {
			// if already loaded throw AlreadyLoadedException
			throw new AlreadyLoadedException("nffg with name " + nffg.getName() + "already exists in the service");
		}
	}

	/**
	 * PUT /policies/{policy_name}
	 * 
	 * @param policy
	 * @throws ServiceException
	 */
	void loadPolicy(Policy policy) throws ServiceException {
		// if some errors with communication with server, throw ServiceException
		// TODO catch 404, ...
		Policy res = target.path("policies").path(policy.getName()).request(MediaType.APPLICATION_XML)
				.put(Entity.entity(policy, MediaType.APPLICATION_XML), Policy.class);
	}

	/**
	 * DELETE /policies/{policyName}
	 * 
	 * @param name
	 * @throws UnknownNameException
	 * @throws ServiceException
	 */
	@Override
	public void unloadReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		// if 404 throw UnknownNameException
		// if other errors throw ServiceException
		Response res = target.path("policies").path(name).request(MediaType.APPLICATION_XML).delete();
		if (res.getStatus() == 404) {
			throw new UnknownNameException(name);
		}
	}

	/**
	 * POST /policies/{policyName}/result
	 * 
	 * @param name
	 * @return
	 * @throws UnknownNameException
	 * @throws ServiceException
	 */
	@Override
	public boolean testReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		// if 404 throw UnknownNameException
		// if other errors throw ServiceException
		// return result.verificationResult
		Policy result = target.path("policies").path(name).path("result").request(MediaType.APPLICATION_XML)
				.post(Entity.entity(null, MediaType.APPLICATION_XML), Policy.class);
		// TODO exceptions
		return result.getResult().isSatisfied();
	}

}
