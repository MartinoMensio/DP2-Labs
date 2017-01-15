package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

public class NotFoundException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NotFoundException(String string) {
		super(Response.status(Response.Status.NOT_FOUND).entity("Not found: " + string).type(MediaType.TEXT_PLAIN).build());
	}

}
