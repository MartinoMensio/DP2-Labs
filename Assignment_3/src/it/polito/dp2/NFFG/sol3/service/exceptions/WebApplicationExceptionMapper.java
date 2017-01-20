package it.polito.dp2.NFFG.sol3.service.exceptions;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

/**
 * This class catches all the WebApplicationException and returns a plaintext
 * message instead of the default HTML error page. For other RuntimeException
 * the default HTML error page is kept to make the debugging easier.
 * 
 * @author Martino Mensio
 *
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	@Override
	public Response toResponse(WebApplicationException e) {
		return Response.status(Response.Status.fromStatusCode(e.getResponse().getStatus())).entity(e.getMessage())
				.type(MediaType.TEXT_PLAIN).build();
	}

}
