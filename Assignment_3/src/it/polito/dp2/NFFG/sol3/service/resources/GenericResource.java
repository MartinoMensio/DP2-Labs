package it.polito.dp2.NFFG.sol3.service.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.sol3.service.*;

/**
 * An abstract resource that has a linked Service and reduces redundancy of code
 * 
 * @author Martino Mensio
 *
 */
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public abstract class GenericResource {

	protected Service service;

	public GenericResource() {
		service = Service.standardService;
		if (service == null) {
			throw new RuntimeException();
			// What the hell
		}
	}
}
