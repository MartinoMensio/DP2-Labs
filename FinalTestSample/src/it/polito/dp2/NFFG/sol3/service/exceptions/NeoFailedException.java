package it.polito.dp2.NFFG.sol3.service.exceptions;

import javax.ws.rs.*;

/**
 * Exception for errors in communication with neo4j
 * 
 * @author Martino Mensio
 *
 */
public class NeoFailedException extends InternalServerErrorException {

	private static final long serialVersionUID = 1L;

	public NeoFailedException(String string) {
		super("InternalServerError caused by neo4j: " + string);
	}

}
