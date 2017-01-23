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
@Path("verifier")
@Api(value = "verifier", description = "verifies policies against the stored nffgs, without storing the policy")
public class VerifierResource extends GenericResource {

	@POST
	@ApiOperation(value = "verify a policy without storing it", notes = "to be called on the child resource of the nffg of reference")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "verification done"),
			@ApiResponse(code = 422, message = "policy is not compliant with the schema or invalid reference to stored resources") })
	public Policy verifyResultOnTheFly(Policy policy) {
		return service.verifyResultOnTheFly(policy);
	}
}
