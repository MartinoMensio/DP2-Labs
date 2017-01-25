package it.polito.dp2.NFFG.sol3.service.exceptions;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * This exception is used when there is a reference to some data that is
 * not stored in the service.
 * 
 * @author Martino Mensio
 *
 */
public class MissingReferenceException extends BadRequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingReferenceException(String string) {
		super("Missing reference: " + string);
	}

}
