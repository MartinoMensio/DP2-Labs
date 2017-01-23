package it.polito.dp2.NFFG.sol3.service.resources;

import java.net.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.wordnik.swagger.annotations.*;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.exceptions.NotFoundException;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * Resource that manages the policies
 * 
 * @author Martino Mensio
 *
 */
@Path("policies")
@Api(value = "policies", description = "collection of policies")
public class PoliciesResource extends GenericResource {

	@GET
	@ApiOperation(value = "get the collection of policies", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	public List<Policy> getPolicies(@QueryParam("nffg") String nffgName) {
		return service.getPolicies(nffgName);
	}

	@PUT
	@ApiOperation(value = "store a policy to a specified resource", notes = "both for creation and update, identified by policy name")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "policy updated"),
			@ApiResponse(code = 201, message = "policy created"),
			@ApiResponse(code = 422, message = "validation of policy failed or or invalid reference to stored resources") })
	@Path("{policy_name}")
	public Response postPolicy(Policy policy, @PathParam("policy_name") String policyName, @Context UriInfo uriInfo) {
		// Check that the policy name is the same in the pathParam and in the
		// request body, because validation is done only on the request body
		if (!policyName.equals(policy.getName())) {
			throw new BadRequestException(
					"The name of the policy must be the same in the path and in the request body");
		}
		Policy old = service.storePolicy(policy);
		if (old == null) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			URI u = builder.path(policy.getName()).build();
			return Response.created(u).entity(policy).build();
		} else {
			return Response.ok(policy).build();
		}
	}

	@GET
	@ApiOperation(value = "get a policy", notes = "identified by its name")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "policy not found") })
	@Path("{policy_name}")
	public Policy getPolicy(@PathParam("policy_name") String policyName) {
		Policy policy = service.getPolicy(policyName);
		if (policy == null) {
			throw new NotFoundException(policyName);
		}
		return policy;
	}

	@DELETE
	@ApiOperation(value = "Delete a policy", notes = "identified by its name")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "policy deleted"),
			@ApiResponse(code = 404, message = "policy not found") })
	@Path("{policy_name}")
	public Response deletePolicy(@PathParam("policy_name") String policyName) {
		Policy policy = service.deletePolicy(policyName);
		if (policy == null) {
			throw new NotFoundException(policyName);
		}
		return Response.ok(policy).build();
	}

	@POST
	@ApiOperation(value = "Evaluate the result of a policy", notes = "The policy is verified using neo4j")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "result updated"),
			@ApiResponse(code = 404, message = "policy not found") })
	@Path("{policy_name}/result")
	public Policy updatePolicyResult(@PathParam("policy_name") String policyName) {
		Policy policy = service.updatePolicyResult(policyName);
		if (policy == null) {
			// policy not found in the service
			throw new NotFoundException(policyName);
		}
		return policy;
	}
}
