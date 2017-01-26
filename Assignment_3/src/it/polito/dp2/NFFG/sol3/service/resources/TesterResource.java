package it.polito.dp2.NFFG.sol3.service.resources;

import javax.ws.rs.*;

import com.wordnik.swagger.annotations.*;

import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * Resource for the verification of policies against stored nffgs. The policies
 * are not stored in the service
 * 
 * @author Martino Mensio
 *
 */
@Path("tester")
@Api(value = "tester", description = "verifies policies against the stored nffgs, without storing the policy")
public class TesterResource extends GenericResource {

	@POST
	@ApiOperation(value = "verify a policy without storing it", notes = "")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "the reachability policy has been tested and the policy is provided back to the client with the updated result"),
			@ApiResponse(code = 400, message = "validation error or invalid reference to stored resources") })
	public Policy verifyResultOnTheFly(Policy policy) {
		return service.verifyResultOnTheFly(policy);
	}
}
