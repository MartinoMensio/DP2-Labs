package it.polito.dp2.NFFG.sol3.service.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("totalNumberOfPolicies")
@Produces(MediaType.TEXT_PLAIN)
public class TotalNumberOfPoliciesResource extends GenericResource {
	
	@GET
	public int getNumberOfPolicies() {
		return service.countPolicies();
	}
}
