package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.wordnik.swagger.annotations.*;

import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * This class is the web interface
 * 
 * @author Martino Mensio
 *
 */

@Path("/")
@Api(value = "/", description = "service for nffg and policies")
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
	@ApiOperation(value = "test", notes ="ahaha")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK")
	})
	@Path("test")
	public String test() {
		return "hello";
	}

	@GET
	@ApiOperation(value = "get the collection of NFFGs", notes ="")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK")
	})
	@Path("nffgs")
	public List<Nffg> getNffgs() {
		// TODO
		return service.getNffgs();
	}

	@POST
	@ApiOperation(value = "store a new NFFG", notes ="the name of the NFFG is the id of the created resource")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "NFFG created"),
			@ApiResponse(code = 409, message = "conflict because of already stored nffg with same name"),
			@ApiResponse(code = 422, message = "validation failed")
	})
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
	@ApiOperation(value = "delete an NFFG", notes ="identifying the NFFG by its name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "NFFG deleted"),
			@ApiResponse(code = 404, message = "NFFG not found")
	})
	@Path("nffgs/{nffg_name}")
	public Response deleteNffg(@PathParam("nffg_name") String nffgName) {
		Nffg nffg = service.deleteNffg(nffgName);
		if(nffg == null) {
			throw new NotFoundException(nffgName);
		}
		return Response.ok(nffg).build();
	}
	
	@POST
	@ApiOperation(value = "verify a policy without storing it", notes ="to be called on the child resource of the nffg of reference")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "verification done"),
			@ApiResponse(code = 404, message = "NFFG not found"),
			@ApiResponse(code = 422, message = "policy is not compliant with the schema")
	})
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
	@ApiOperation(value = "get a single NFFG", notes ="identified by its name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "NFFG not found")
	})
	@Path("nffgs/{nffg_name}")
	public Nffg getNffg(@PathParam("nffg_name") String nffgName) throws NotFoundException {
		Nffg result = service.getNffg(nffgName);
		if (result == null) {
			throw new NotFoundException(nffgName);
		}
		return result;
	}
	
	@PUT
	@ApiOperation(value = "store a policy to a specified resource", notes ="both for creation and update, identified by policy name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "policy updated"),
			@ApiResponse(code = 201, message = "policy created"),
			@ApiResponse(code = 412, message = "the referred nffg does not exist"),
			@ApiResponse(code = 422, message = "validation of policy failed")
	})
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
	@ApiOperation(value = "get the collection of policies", notes ="")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK")
	})
	@Path("policies")
	public List<Policy> getPolicies() {
		return service.getPolicies();
	}
	
	@GET
	@ApiOperation(value = "get a policy", notes ="identified by its name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "policy not found")
	})
	@Path("policies/{policy_name}")
	public Policy getPolicy(@PathParam("policy_name") String policyName) {
		Policy policy = service.getPolicy(policyName);
		if(policy == null) {
			throw new NotFoundException(policyName);
		}
		return policy;
	}
	
	@DELETE
	@ApiOperation(value = "Delete a policy", notes ="identified by its name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "policy deleted"),
			@ApiResponse(code = 404, message = "policy not found")
	})
	@Path("policies/{policy_name}")
	public Response deletePolicy(@PathParam("policy_name") String policyName) {
		Policy policy = service.deletePolicy(policyName);
		if(policy == null) {
			throw new NotFoundException(policyName);
		}
		return Response.ok(policy).build();
	}
	
	@POST
	@ApiOperation(value = "Evaluate the result of a policy", notes ="The policy is verified using neo4j")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "result updated"),
			@ApiResponse(code = 404, message = "policy not found")
	})
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
