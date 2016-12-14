package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.*;

@Path("test")
public class Resource {

	@GET
	public String test() {
		return "hello";
	}
}
