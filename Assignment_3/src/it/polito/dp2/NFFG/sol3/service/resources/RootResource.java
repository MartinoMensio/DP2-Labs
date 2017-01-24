package it.polito.dp2.NFFG.sol3.service.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/")
public class RootResource extends GenericResource {

	@DELETE
	public Response deleteAll() {
		service.deleteAll();
		return Response.noContent().build();
	}
}
