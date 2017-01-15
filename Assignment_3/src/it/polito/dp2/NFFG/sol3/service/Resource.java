package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

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

	public Resource() {
		service = Service.standardService;
		if (service == null) {
			throw new RuntimeException();
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
		// TODO validate nffg
		if (nffg == null) {
			// validation error
			throw new ValidationFailedException("nffg is not compliant with the schema");
		}
		Nffg response = service.storeNffg(nffg);
		if (response != null) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			URI u = builder.path(response.getName()).build();
			return Response.created(u).entity(response).build();
		} else
			throw new ConflictException("nffg already stored with the name " + nffg.getName());
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
	public Policy verifyResultOnTheFly(Policy policy, @PathParam("nffg_name") String nffgName) {
		Policy result = service.verifyResultOnTheFly(policy, nffgName);
		if(result == null) {
			// TODO reason: could be because of nffgName or because other references or other errors in body request
			throw new NotFoundException(nffgName);
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
		if(policy == null) {
			throw new ValidationFailedException("policy is not compliant with the schema");
		}
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
	public Policy updatePolicyResult(@PathParam("policy_name") String policyName) {
		Policy policy = service.updatePolicyResult(policyName);
		if(policy == null) {
			// TODO possible notFound or internal error because of neo4j
			throw new NotFoundException(policyName);
		}
		return policy;
	}
	
}
