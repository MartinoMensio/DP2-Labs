package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.ExceptionMapper;

public class ValidationFailedException extends Exception implements ExceptionMapper<ValidationFailedException> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationFailedException(String string) {
		// TODO Auto-generated constructor stub
		super(string);
	}

	@Override
	public Response toResponse(ValidationFailedException exception) {
		// TODO Auto-generated method stub
		return Response.status(422).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}

}
