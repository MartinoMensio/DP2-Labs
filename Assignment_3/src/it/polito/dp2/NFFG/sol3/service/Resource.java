package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * This class is the web interface
 * 
 * @author Martino Mensio
 *
 */

@Path("/")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class Resource {
	
	private Service service;
	
	public Resource() {
		try {
			service = Service.createService();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// What the hell
		}
	}

	@GET
	@Path("test")
	public String test() {
		return "hello";
	}

	@GET
	@Path("nffgs")
	public List<NffgT> getNffgs() {
		// TODO
		return service.getNffgs();
	}

	@POST
	@Path("nffgs")
	public Response postNffg(NffgT nffg, @Context UriInfo uriInfo) {
		NffgT response = service.storeNffg(nffg);
		if (response != null) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        	URI u = builder.path(response.getName()).build();
        	return Response.created(u).entity(response).build();
		}
		else throw new ForbiddenException("something wrong");
	}
	
}
