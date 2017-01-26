package it.polito.dp2.NFFG.sol3.service.resources;

import java.net.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.wordnik.swagger.annotations.*;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * Resource that manages the nffgs
 * 
 * @author Martino Mensio
 *
 */
@Path("nffgs")
@Api(value = "nffgs", description = "collection of nffgs")
public class NffgsResource extends GenericResource {

	@GET
	@ApiOperation(value = "get the collection of NFFGs", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK, in the response the set of NFFGs") })
	public List<Nffg> getNffgs() {
		return service.getNffgs();
	}

	@POST
	@ApiOperation(value = "store a new NFFG", notes = "the name of the NFFG is the id of the created resource")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "the NFFG has been created and is provided back to the client"),
			@ApiResponse(code = 400, message = "schema validation error"),
			@ApiResponse(code = 409, message = "a NFFG with the same name is already stored") })
	public Response postNffg(Nffg nffg, @Context UriInfo uriInfo) {
		Nffg response = service.storeNffg(nffg);
		if (response != null) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			URI u = builder.path(response.getName()).build();
			return Response.created(u).entity(response).build();
		} else
			throw new ConflictException("nffg already stored with the name " + nffg.getName());
	}

	@GET
	@ApiOperation(value = "get a single NFFG", notes = "identified by its name")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK, in the response the NFFG"),
			@ApiResponse(code = 404, message = "no NFFG exists with this name") })
	@Path("{nffg_name}")
	public Nffg getNffg(@PathParam("nffg_name") String nffgName) {
		Nffg result = service.getNffg(nffgName);
		if (result == null) {
			throw new NotFoundException("Nffg with name " + nffgName + " is not stored in the service");
		}
		return result;
	}

	@DELETE
	@ApiOperation(value = "delete an NFFG", notes = "identifying the NFFG by its name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "the NFFG has been deleted and is provided back to the client"),
			@ApiResponse(code = 403, message = "impossible to delete the NFFG because some policies refer to it"),
			@ApiResponse(code = 404, message = "no NFFG exists with this name") })
	@Path("{nffg_name}")
	public Response deleteNffg(@PathParam("nffg_name") String nffgName,
			@DefaultValue("false") @QueryParam("force") Boolean force) {
		Nffg nffg = service.deleteNffg(nffgName, force);
		if (nffg == null) {
			throw new NotFoundException("Policy with name " + nffgName + " is not stored in the service");
		}
		return Response.ok(nffg).build();
	}

}
