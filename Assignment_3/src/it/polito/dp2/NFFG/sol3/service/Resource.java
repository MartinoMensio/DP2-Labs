package it.polito.dp2.NFFG.sol3.service;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * This class is the web interface
 * 
 * @author Martino Mensio
 *
 */

@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class Resource {
	
	private Service service = new Service();

	@Path("test")
	@GET
	public String test() {
		return "hello";
	}

	@Path("nffgs")
	@GET
	public List<NffgT> getNffgs() {
		// TODO
		return service.getNffgs();
	}

	@Path("nffgs")
	public Response postNffg(NffgT nffg) {
		// TODO: call Service.storeNffg(nffg)
		return service.getNffg();
	}
	
}
