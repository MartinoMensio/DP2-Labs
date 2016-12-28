package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;
import java.util.stream.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;

/**
 * This class is the web interface
 * 
 * @author Martino Mensio
 *
 */

@Path("/")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class Resource {

	private Service service;

	public Resource() {
		try {
			service = Service.createService();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
			// What the hell
		}
	}

	@GET
	@Path("test")
	public String test() {
		return "hello";
	}

	@GET
	@Path("nffgs")
	public List<NffgT> getNffgs() {
		// TODO
		return service.getNffgs();
	}

	@POST
	@Path("nffgs")
	public Response postNffg(NffgT nffg, @Context UriInfo uriInfo) {
		NffgT response = service.storeNffg(nffg);
		if (response != null) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			URI u = builder.path(response.getName()).build();
			return Response.created(u).entity(response).build();
		} else
			throw new ForbiddenException("something wrong");
	}
	
	@GET
	@Path("nffgs/{nffg_name}")
	public NffgT getNffg(@PathParam("nffg_name") String nffgName) throws NotFoundException {
		NffgT result = service.getNffg(nffgName);
		if (result == null) {
			throw new NotFoundException(nffgName);
		}
		return result;
	}
	
	@GET
	@Path("nffgs/{nffg_name}/nodes")
	public List<NodeT> getNodes(@PathParam("nffg_name") String nffgName) throws NotFoundException {
		NffgT nffg = service.getNffg(nffgName);
		if (nffg == null) {
			throw new NotFoundException(nffgName);
		}
		return nffg.getNode();
	}
	
	@GET
	@Path("nffgs/{nffg_name}/nodes/{node_name}")
	public NodeT getNode(@PathParam("nffg_name") String nffgName, @PathParam("node_name") String nodeName) throws NotFoundException {
		NffgT nffg = service.getNffg(nffgName);
		if (nffg == null) {
			throw new NotFoundException(nffgName);
		}
		Optional<NodeT> node = nffg.getNode().stream().filter(n -> n.getName().equals(nodeName)).findAny();
		if(!node.isPresent()) {
			throw new NotFoundException(nodeName);
		}
		return node.get();
	}
	
	@GET
	@Path("nffgs/{nffg_name}/nodes/{node_name}/links")
	public List<LinkT> getNodeLinks(@PathParam("nffg_name") String nffgName, @PathParam("node_name") String nodeName) throws NotFoundException {
		NffgT nffg = service.getNffg(nffgName);
		if (nffg == null) {
			throw new NotFoundException(nffgName);
		}
		Optional<NodeT> node = nffg.getNode().stream().filter(n -> n.getName().equals(nodeName)).findAny();
		if(!node.isPresent()) {
			throw new NotFoundException(nodeName);
		}
		return nffg.getLink().stream().filter(l -> l.getSrc().getRef().equals(nodeName)).collect(Collectors.toList());
	}
	
	@GET
	@Path("nffgs/{nffg_name}/links/{link_name}/src")
	public NodeT getLinkSrcNode(@PathParam("nffg_name") String nffgName, @PathParam("link_name") String linkName) throws NotFoundException {
		NffgT nffg = service.getNffg(nffgName);
		if (nffg == null) {
			throw new NotFoundException(nffgName);
		}
		Optional<LinkT> link = nffg.getLink().stream().filter(l -> l.getName().equals(linkName)).findAny();
		if(!link.isPresent()) {
			throw new NotFoundException(linkName);
		}
		Optional<NodeT> src = nffg.getNode().stream().filter(n -> n.getName().equals(link.get().getSrc().getRef())).findAny();
		if(!src.isPresent()) {
			throw new InternalServerErrorException(link.get().getName());
		}
		return src.get();
	}
	
	@GET
	@Path("nffgs/{nffg_name}/links/{link_name}/dst")
	public NodeT getLinkDstNode(@PathParam("nffg_name") String nffgName, @PathParam("link_name") String linkName) throws NotFoundException {
		NffgT nffg = service.getNffg(nffgName);
		if (nffg == null) {
			throw new NotFoundException(nffgName);
		}
		Optional<LinkT> link = nffg.getLink().stream().filter(l -> l.getName().equals(linkName)).findAny();
		if(!link.isPresent()) {
			throw new NotFoundException(linkName);
		}
		Optional<NodeT> dst = nffg.getNode().stream().filter(n -> n.getName().equals(link.get().getDst().getRef())).findAny();
		if(!dst.isPresent()) {
			throw new InternalServerErrorException(link.get().getName());
		}
		return dst.get();
	}

	@POST
	@Path("nffgs/{nffg_name}/policies")
	public Response postPolicy(PolicyT policy, @PathParam("nffg_name") String nffgName, @Context UriInfo uriInfo) {
		PolicyT response = service.storePolicy(policy);
		// TODO distinguish if already stored or not ??
		if (response != null) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			URI u = builder.path(response.getName()).build();
			return Response.created(u).entity(service.verifyPolicy(response)).build();
		} else
			throw new ForbiddenException("something wrong");
	}
	
	@GET
	@Path("policies")
	public List<PolicyT> getPolicies() {
		return service.getPolicies();
	}
}
