package it.polito.dp2.NFFG.sol3.service.exceptions;

import javax.ws.rs.core.*;

public class NeoFailedException extends javax.ws.rs.InternalServerErrorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NeoFailedException(String string) {
		super("InternalServerError caused by neo4j: " + string);
	}

}
