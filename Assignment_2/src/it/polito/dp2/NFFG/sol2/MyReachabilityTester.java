package it.polito.dp2.NFFG.sol2;

import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.*;

public class MyReachabilityTester implements ReachabilityTester {

	private NffgVerifier monitor;
	private URI uri;
	private String graphName;
	private WebTarget target;
	private Map<String, String> nodeIds;
	private Map<LinkReader, String> linkIds;
	
	private ObjectFactory factory = new ObjectFactory();

	public MyReachabilityTester(NffgVerifier nffgR, URI uri) {
		this.monitor = nffgR;
		this.uri = uri;
		graphName = null;
		target = ClientBuilder.newClient().target(uri.toString()).path("resource");
	}

	@Override
	public void loadNFFG(String name) throws UnknownNameException, ServiceException {
		NffgReader nffgR = monitor.getNffg(name);
		if (nffgR == null) {
			throw new UnknownNameException("no nffg with name " + name);
		}
		// clean the graphName (for failures)
		graphName = null;

		// deletion of previous graph (nodes and links)
		try {
			Response resdel = target.path("nodes").request(MediaType.APPLICATION_XML).delete();

			if (resdel.getStatus() != 200) {
				// not successful
				throw new ServiceException("impossible to delete the previous graph: HTTP " + resdel.getStatus());
			}
		} catch (ResponseProcessingException e) {
			// e.g. if the MediaType is wrong
			throw new ServiceException("impossible to delete the previous graph because of response processing error"
					+ ", status: HTTP " + e.getResponse().getStatus());
		}

		// create new map of node ids
		nodeIds = new HashMap<>();

		// node uploading
		for (NodeReader nodeR : nffgR.getNodes()) {
			Node node = factory.createNode();
			Property nameP = factory.createProperty();
			nameP.setName("name");
			nameP.setValue(nodeR.getName());
			node.getProperty().add(nameP);
			try {
				Node res = target.path("node").request(MediaType.APPLICATION_XML)
						.post(Entity.entity(node, MediaType.APPLICATION_XML), Node.class);

				nodeIds.put(nodeR.getName(), res.getId());
			} catch (ResponseProcessingException e) {
				throw new ServiceException("response processing error with node named " + nodeR.getName()
						+ ", status: HTTP " + e.getResponse().getStatus());
			} catch (ProcessingException e) {
				throw new ServiceException("processing error with node named " + nodeR.getName());
			} catch (WebApplicationException e) {
				throw new ServiceException("webapp error on node " + nodeR.getName() + " because of error  HTTP "
						+ e.getResponse().getStatus());
			}

		}

		// create new map of link id
		linkIds = new HashMap<>();

		// TODO refactor links uploading
		for (LinkReader linkR : nffgR.getNodes().stream().flatMap(n -> n.getLinks().stream())
				.collect(Collectors.toList())) {
			Relationship relation = factory.createRelationship();
			relation.setDstNode(nodeIds.get(linkR.getDestinationNode().getName()));
			relation.setType("Connection");

			try {
				Relationship res = target.path("node").path(nodeIds.get(linkR.getSourceNode().getName()))
						.path("relationship").request(MediaType.APPLICATION_XML)
						.post(Entity.entity(relation, MediaType.APPLICATION_XML), Relationship.class);

				linkIds.put(linkR, res.getId());
				// System.out.println(res.getId());
			} catch (ResponseProcessingException e) {
				throw new ServiceException("response processing error with link named " + linkR.getName()
						+ ", status: HTTP " + e.getResponse().getStatus());
			} catch (ProcessingException e) {
				throw new ServiceException("processing error with link named " + linkR.getName());
			} catch (WebApplicationException e) {
				throw new ServiceException("webapp error on link " + linkR.getName() + " because of error HTTP "
						+ e.getResponse().getStatus());
			}
		}

		// on success, overwrite the graphName
		graphName = name;
	}

	@Override
	public boolean testReachability(String srcName, String destName)
			throws UnknownNameException, ServiceException, NoGraphException {
		if (graphName == null) {
			throw new NoGraphException("no graph is currently loaded, please call loadNFFG");
		}
		String srcId = nodeIds.get(srcName);
		String dstId = nodeIds.get(destName);

		if (srcId == null) {
			throw new UnknownNameException("no node with name " + srcName);
		}
		if (dstId == null) {
			throw new UnknownNameException("no node with name " + destName);
		}

		// request the paths from source to destination
		try {
			Paths res = target.path("node").path(srcId).path("paths").queryParam("dst", dstId)
					.request(MediaType.APPLICATION_XML).get(Paths.class);
			// System.out.println("found " + res.getPath().size() + "paths");
			return res.getPath().size() > 0;
		} catch (ResponseProcessingException e) {
			throw new ServiceException(
					"impossible to process the Paths response: response processing error, status: HTTP "
							+ e.getResponse().getStatus());
		} catch (ProcessingException e) {
			throw new ServiceException("impossible to process the Paths response: processing error");
		} catch (WebApplicationException e) {
			throw new ServiceException(
					"impossible to process the Paths response: webapp error HTTP " + e.getResponse().getStatus());
		}
	}

	@Override
	public String getCurrentGraphName() {
		return graphName;
	}

}
