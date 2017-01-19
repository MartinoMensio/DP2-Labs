package it.polito.dp2.NFFG.sol3.service.resources;

import java.net.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.wordnik.swagger.annotations.*;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.exceptions.NotFoundException;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

@Path("policies")
@Api(value = "policies", description = "collection of policies")
public class PoliciesResource extends GenericResource {

	@GET
	@ApiOperation(value = "get the collection of policies", notes ="")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK")
	})
	public List<Policy> getPolicies() {
		return service.getPolicies();
	}
	
	@PUT
	@ApiOperation(value = "store a policy to a specified resource", notes ="both for creation and update, identified by policy name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "policy updated"),
			@ApiResponse(code = 201, message = "policy created"),
			@ApiResponse(code = 422, message = "validation of policy failed or or invalid reference to stored resources")
	})
	@Path("{policy_name}")
	public Response postPolicy(Policy policy, @PathParam("policy_name") String policyName, @Context UriInfo uriInfo) {
		// TODO validate policy
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
	@ApiOperation(value = "get a policy", notes ="identified by its name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "policy not found")
	})
	@Path("{policy_name}")
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
	@Path("{policy_name}")
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
	@Path("{policy_name}/result")
	public Policy updatePolicyResult(@PathParam("policy_name") String policyName) {
		Policy policy = service.updatePolicyResult(policyName);
		if(policy == null) {
			// TODO possible notFound or internal error because of neo4j
			throw new NotFoundException(policyName);
		}
		return policy;
	}
}
