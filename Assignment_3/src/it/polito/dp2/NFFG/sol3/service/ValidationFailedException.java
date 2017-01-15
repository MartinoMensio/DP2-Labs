package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * This class handles the HTTP 422 status code, that is meant for requests that
 * have right syntax but are semantically incorrect. See
 * <a href="https://tools.ietf.org/html/rfc4918#section-11.2">corresponding
 * RFC</a>
 * 
 * @author Martino Mensio
 *
 */
public class ValidationFailedException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationFailedException(String string) {
		super(Response.status(422).entity("Validation failed: " + string).type(MediaType.TEXT_PLAIN).build());
	}

}
