package it.polito.dp2.NFFG.sol3.service.exceptions;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * This class handles the HTTP 422 status code, that is meant for requests that
 * have right syntax but are semantically incorrect. See
 * <a href="https://tools.ietf.org/html/rfc4918#section-11.2">corresponding
 * RFC</a> This includes validation error of requests and checking to references
 * to data already stored into the service
 * 
 * @author Martino Mensio
 *
 */
public class ValidationFailedException extends ClientErrorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationFailedException(String string) {
		// need all this because 422 is not included in enum Response.Status
		super(Response.status(422).entity("Validation failed: " + string).type(MediaType.TEXT_PLAIN).build());
	}

}
