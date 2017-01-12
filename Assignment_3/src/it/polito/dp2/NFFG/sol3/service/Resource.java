package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;
import java.util.stream.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Link;

/**
 * This class is the web interface
 * 
 * @author Martino Mensio
 *
 */

@Path("/")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class Resource {

	private Service service;
	private ObjectFactory factory;

	public Resource() {
		factory = new ObjectFactory();
		try {
			service = Service.createService();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
			// What the hell
		}
	}

	@GET
	@Path("test")
	public String test() {
		return "hello";
	}

	@GET
	@Path("nffgs")
	public List<Nffg> getNffgs() {
		// TODO
		return service.getNffgs();
	}

	@POST
	@Path("nffgs")
	public Response postNffg(Nffg nffg, @Context UriInfo uriInfo) {
		Nffg response = service.storeNffg(nffg);
		if (response != null) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			URI u = builder.path(response.getName()).build();
			return Response.created(u).entity(response).build();
		} else
			throw new ForbiddenException("something wrong");
	}
	
	@DELETE
	@Path("nffgs/{nffg_name}")
	public Response deleteNffg(@PathParam("nffg_name") String nffgName) {
		Nffg nffg = service.deleteNffg(nffgName);
		if(nffg == null) {
			throw new NotFoundException(nffgName);
		}
		return Response.ok().build();
	}
	
	@POST
	@Path("nffgs/{nffg_name}/online_result")
	public Result verifyResultOnTheFly(Policy policy, @PathParam("nffg_name") String nffgName) {
		Result result = service.verifyResultOnTheFly(policy, nffgName);
		if(result == null) {
			// TODO reason: could be because of nffgName or because other references or other errors in body request
			throw new NotFoundException();
		}
		return result;
	}
	
	@GET
	@Path("nffgs/{nffg_name}")
	public Nffg getNffg(@PathParam("nffg_name") String nffgName) throws NotFoundException {
		Nffg result = service.getNffg(nffgName);
		if (result == null) {
			throw new NotFoundException(nffgName);
		}
		return result;
	}
	
	@PUT
	@Path("policies/{policy_name}")
	public Response postPolicy(Policy policy, @PathParam("policy_name") String policyName, @Context UriInfo uriInfo) {
		// Overwrite the policy name in the request
		policy.setName(policyName);
		Policy response = service.storePolicy(policy);
		// TODO distinguish if already stored or not ??
		if (response != null) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			URI u = builder.path(response.getName()).build();
			return Response.created(u).entity(response).build();
		} else
			throw new ForbiddenException("something wrong");
	}
	
	@GET
	@Path("policies")
	public List<Policy> getPolicies() {
		return service.getPolicies();
	}
	
	@GET
	@Path("policies/{policy_name}")
	public Policy getPolicy(@PathParam("policy_name") String policyName) {
		Policy policy = service.getPolicy(policyName);
		if(policy == null) {
			throw new NotFoundException(policyName);
		}
		return policy;
	}
	
	@DELETE
	@Path("policies/{policy_name}")
	public Response deletePolicy(@PathParam("policy_name") String policyName) {
		Policy policy = service.deletePolicy(policyName);
		if(policy == null) {
			throw new NotFoundException(policyName);
		}
		return Response.ok().build();
	}
	
	@POST
	@Path("policies/{policy_name}/result")
	public Result updatePolicyResult(@PathParam("policy_name") String policyName) {
		Policy policy = service.getPolicy(policyName);
		if(policy == null) {
			throw new NotFoundException(policyName);
		}
		policy = service.verifyPolicy(policy);
		if(policy == null) {
			// TODO maybe for src or dst not found
			throw new ForbiddenException();
		}
		return policy.getResult();
	}
	
}
