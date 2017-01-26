package it.polito.dp2.NFFG.sol3.service.exceptions;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * This exception is meant for telling the client that the request cannot be
 * fulfilled because an entity with the same name already exists.
 * 
 * @author Martino Mensio
 *
 */
public class ConflictException extends ClientErrorException {

	private static final long serialVersionUID = 1L;

	public ConflictException(String string) {
		super("Conflict: " + string, Response.Status.CONFLICT);
	}
}
