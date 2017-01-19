package it.polito.dp2.NFFG.sol3.service.resources;

import java.net.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.wordnik.swagger.annotations.*;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.exceptions.NotFoundException;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

@Path("nffgs")
@Api(value = "nffgs", description = "collection of nffgs")
public class NffgsResource extends GenericResource {

	@GET
	@ApiOperation(value = "get the collection of NFFGs", notes ="")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK")
	})
	public List<Nffg> getNffgs() {
		return service.getNffgs();
	}
	
	@POST
	@ApiOperation(value = "store a new NFFG", notes ="the name of the NFFG is the id of the created resource")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "NFFG created"),
			@ApiResponse(code = 409, message = "conflict because of already stored nffg with same name"),
			@ApiResponse(code = 422, message = "validation failed")
	})
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
	
	@GET
	@ApiOperation(value = "get a single NFFG", notes ="identified by its name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "NFFG not found")
	})
	@Path("{nffg_name}")
	public Nffg getNffg(@PathParam("nffg_name") String nffgName) throws NotFoundException {
		Nffg result = service.getNffg(nffgName);
		if (result == null) {
			throw new NotFoundException(nffgName);
		}
		return result;
	}
	
	@DELETE
	@ApiOperation(value = "delete an NFFG", notes ="identifying the NFFG by its name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "NFFG deleted"),
			@ApiResponse(code = 404, message = "NFFG not found")
	})
	@Path("{nffg_name}")
	public Response deleteNffg(@PathParam("nffg_name") String nffgName) {
		Nffg nffg = service.deleteNffg(nffgName);
		if(nffg == null) {
			throw new NotFoundException(nffgName);
		}
		return Response.ok(nffg).build();
	}

}
