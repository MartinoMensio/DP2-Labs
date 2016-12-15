package it.polito.dp2.NFFG.sol3.service;

import java.util.*;

import javax.ws.rs.*;

import it.polito.dp2.NFFG.sol3.jaxb.*;

/**
 * This class is the web interface
 * 
 * @author Martino Mensio
 *
 */
public class Resource {

	@Path("test")
	@GET
	public String test() {
		return "hello";
	}

	@Path("nffgs")
	@GET
	public List<NffgT> getNffgs() {

	}
}
