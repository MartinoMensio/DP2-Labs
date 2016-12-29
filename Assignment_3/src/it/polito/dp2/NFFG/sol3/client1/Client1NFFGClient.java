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
			Response res = target.path("nffgs").request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(nffg, MediaType.APPLICATION_JSON));
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
		for (PolicyReader policyR : verifier.getPolicies()) {
			ReachabilityPolicyReader reachPolicyR = (ReachabilityPolicyReader)policyR;

			PolicyT policy = new PolicyT();
			policy.setName(policyR.getName());
			policy.setNffg(policyR.getNffg().getName());
			policy.setPositive(policyR.isPositive());
			NodeRefT src = new NodeRefT();
			src.setRef(reachPolicyR.getSourceNode().getName());
			policy.setSrc(src);
			NodeRefT dst = new NodeRefT();
			dst.setRef(reachPolicyR.getSourceNode().getName());
			policy.setDst(dst);
			VerificationResultReader resultR = policyR.getResult();
			if(resultR != null) {
				ResultT result = new ResultT();
				result.setContent(resultR.getVerificationResultMsg());
				result.setSatisfied(resultR.getVerificationResult());
				result.setVerified(Utils.XMLGregorianCalendarFromCalendar(resultR.getVerificationTime()));
				policy.setResult(result);
			}
			loadPolicy(policy);
		}
	}

	@Override
	public void loadReachabilityPolicy(String name, String nffgName, boolean isPositive, String srcNodeName,
			String dstNodeName) throws UnknownNameException, ServiceException {
		
		System.out.println("load called with name:"+name+" nffgName:"+nffgName);

		// if nffgName or srcNodeName or dstNodeName don't correspond to local info, throw UnknownNameException
		NffgReader nffgR = verifier.getNffg(nffgName);
		if(nffgR == null) {
			System.err.println("nffgR null: " + nffgName);
			throw new UnknownNameException(nffgName);
		}
		System.out.println("the nodes of nffg "+nffgName+ " are:");
		for(NodeReader n : nffgR.getNodes()) {
			System.out.print(n.getName() + " ");
		}
		NodeReader src = nffgR.getNode(srcNodeName);
		if(src == null) {
			System.err.println("src null: " + srcNodeName);
			throw new UnknownNameException(srcNodeName);
		}
		NodeReader dst = nffgR.getNode(dstNodeName);
		if(dst == null) {
			System.err.println("dst null: " + dstNodeName);
			throw new UnknownNameException(dstNodeName);
		}
		// build a PolicyT from the values
		PolicyT policy = new PolicyT();
		policy.setName(name);
		NodeRefT srcRef = new NodeRefT();
		NodeRefT dstRef = new NodeRefT();
		srcRef.setRef(srcNodeName);
		dstRef.setRef(dstNodeName);
		policy.setSrc(srcRef);
		policy.setDst(dstRef);
		policy.setNffg(nffgName);
		policy.setPositive(isPositive);
		loadPolicy(policy);
}
	
	void loadPolicy(PolicyT policy) throws ServiceException {
		// POST /nffgs/{nffgName}/policies
		// if some errors with communication with server, throw ServiceException
		// TODO catch 404, ...
		PolicyT res = target.path("nffgs").path(policy.getNffg()).path("policies").request(MediaType.APPLICATION_JSON).post(Entity.entity(policy, MediaType.APPLICATION_JSON), PolicyT.class);
	}

	@Override
	public void unloadReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		// DELETE /policies/{policyName}
		// if 404 throw UnknownNameException
		// if other errors throw ServiceException
		Response res = target.path("policies").path(name).request(MediaType.APPLICATION_JSON).delete();
		if (res.getStatus() == 404) {
			throw new UnknownNameException(name);
		}
	}

	@Override
	public boolean testReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		// POST /policies/{policyName}/result/update
		// if 404 throw UnknownNameException
		// if other errors throw ServiceException
		// return result.verificationResult
		ResultT result = target.path("policies").path(name).path("result").path("update").request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON), ResultT.class);
		// TODO exceptions
		return result.isSatisfied();
	}

}
