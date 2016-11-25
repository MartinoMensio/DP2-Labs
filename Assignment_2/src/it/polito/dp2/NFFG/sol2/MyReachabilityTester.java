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
	private Client client;
	private Map<String, String> nodeIds;
	private Map<LinkReader, String> linkIds;

	public MyReachabilityTester(NffgVerifier nffgR, URI uri) {
		this.monitor = nffgR;
		this.uri = uri;
		graphName = null;
		client = ClientBuilder.newClient();
	}

	@Override
	public void loadNFFG(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		NffgReader nffgR = monitor.getNffg(name);
		if (nffgR == null) {
			throw new UnknownNameException("no nffg with name " + name);
		}
		// clean the graphName (for failures)
		graphName = null;

		// TODO refactor deletion previous graph
		Response resdel = client.target(uri.toString()).path("resource").path("nodes").request(MediaType.TEXT_XML)
				.accept("*/*").delete();
		if (resdel.getStatus() != 200) {
			throw new ServiceException("impossible to delete the previous graph");
		}

		// TODO refactor node uploading
		nodeIds = new HashMap<>();

		for (NodeReader nodeR : nffgR.getNodes()) {
			Node node = new Node();
			Property nameP = new Property();
			nameP.setName("name");
			nameP.setValue(nodeR.getName());
			node.getProperty().add(nameP);
			try {
				Node res = client.target(uri.toString()).path("resource").path("node").request(MediaType.TEXT_XML)
						.accept("*/*").post(Entity.entity(node, MediaType.APPLICATION_XML), Node.class);

				nodeIds.put(nodeR.getName(), res.getId());
			} catch (ResponseProcessingException e) {
				throw new ServiceException("response processing error with node named " + nodeR.getName());
			} catch (ProcessingException e) {
				throw new ServiceException("processing error with node named " + nodeR.getName());
			} catch (WebApplicationException e) {
				throw new ServiceException("webapp error on node " + nodeR.getName() + " because of " + e.getMessage());
			}

		}

		linkIds = new HashMap<>();

		// TODO refactor links uploading
		for (LinkReader linkR : nffgR.getNodes().stream().flatMap(n -> n.getLinks().stream())
				.collect(Collectors.toList())) {
			Relationship relation = new Relationship();
			relation.setDstNode(nodeIds.get(linkR.getDestinationNode().getName()));
			relation.setType("Connection");

			try {
				Relationship res = client.target(uri.toString()).path("resource").path("node")
						.path(nodeIds.get(linkR.getSourceNode().getName())).path("relationship")
						.request(MediaType.TEXT_XML).accept("*/*")
						.post(Entity.entity(relation, MediaType.APPLICATION_XML), Relationship.class);

				linkIds.put(linkR, res.getId());
				// System.out.println(res.getId());
			} catch (ResponseProcessingException e) {
				throw new ServiceException("response processing error with link named " + linkR.getName());
			} catch (ProcessingException e) {
				throw new ServiceException("processing error with link named " + linkR.getName());
			} catch (WebApplicationException e) {
				throw new ServiceException("webapp error on link " + linkR.getName() + " because of " + e.getMessage());
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

		// TODO refactor
		try {
			Paths res = client.target(uri.toString()).path("resource").path("node").path(srcId).path("paths")
					.queryParam("dst", dstId).request(MediaType.TEXT_XML).accept("*/*").get(Paths.class);
			// System.out.println("found " + res.getPath().size() + "paths");

			return res.getPath().size() > 0;
		} catch (ResponseProcessingException e) {
			throw new ServiceException("impossible to process the Paths response: response processing error");
		} catch (ProcessingException e) {
			throw new ServiceException("impossible to process the Paths response: processing error");
		} catch (WebApplicationException e) {
			throw new ServiceException("impossible to process the Paths response: webapp error");
		}
	}

	@Override
	public String getCurrentGraphName() {
		return graphName;
	}

}
