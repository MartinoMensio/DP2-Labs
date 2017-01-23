package it.polito.dp2.NFFG.sol3.service.exceptions;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * This class handles the HTTP 422 status code, that is meant for requests that
 * have right syntax but are semantically incorrect. See
 * <a href="https://tools.ietf.org/html/rfc4918#section-11.2">corresponding
 * RFC</a> This exception is used when there is a reference to some data that is
 * not stored in the service.
 * 
 * @author Martino Mensio
 *
 */
public class MissingReferenceException extends ClientErrorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingReferenceException(String string) {
		// need all this because 422 is not included in enum Response.Status
		super(Response.status(422).entity("Missing reference: " + string).type(MediaType.TEXT_PLAIN).build());
	}

}
