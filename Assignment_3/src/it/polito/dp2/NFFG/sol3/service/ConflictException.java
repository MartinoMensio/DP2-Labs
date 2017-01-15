package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * This exception is meant for telling the client that the request cannot be
 * fulfilled because an entity with the same name already exists.
 * 
 * @author Martino Mensio
 *
 */
public class ConflictException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConflictException(String string) {
		super(Response.status(Response.Status.CONFLICT).entity("Conflict: " + string).type(MediaType.TEXT_PLAIN)
				.build());
	}
}
