package it.polito.dp2.NFFG.sol3.service.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.wordnik.swagger.annotations.*;

/**
 * 
 * @author Martino Mensio
 *
 */
@Path("/")
public class RootResource extends GenericResource {

	@DELETE
	@ApiOperation(value = "delete all the data from the service", notes = "both NFFGs and policies")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "all the data has been deleted") })
	public Response deleteAll() {
		service.deleteAll();
		return Response.noContent().build();
	}
}
