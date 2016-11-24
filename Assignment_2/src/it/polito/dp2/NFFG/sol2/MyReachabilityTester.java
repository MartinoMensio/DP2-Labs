package it.polito.dp2.NFFG.sol2;

import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.*;

public class MyReachabilityTester implements ReachabilityTester {

	private NffgVerifier monitor;
	private URI uri;
	private String graphName;

	public MyReachabilityTester(NffgVerifier nffgR, URI uri) {
		this.monitor = nffgR;
		this.uri = uri;
		graphName = null;
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

		Client client = ClientBuilder.newClient();

		// TODO refactor deletion previous graph
		Response resdel = client.target(uri.toString()).path("resource").path("nodes").request(MediaType.TEXT_XML)
				.accept("*/*").delete();
		if (resdel.getStatus() != 200) {
			throw new ServiceException("impossible to delete the previous graph");
		}

		// TODO refactor node uploading
		Map<NodeReader, String> nodeIds = new HashMap<>();

		for (NodeReader nodeR : nffgR.getNodes()) {
			Node node = new Node();
			Property nameP = new Property();
			nameP.setName("name");
			nameP.setValue(nodeR.getName());
			node.getProperty().add(nameP);
			try {
				Node res = client.target(uri.toString()).path("resource").path("node").request(MediaType.TEXT_XML)
						.accept("*/*").post(Entity.entity(node, MediaType.APPLICATION_XML), Node.class);

				nodeIds.put(nodeR, res.getId());
			} catch (ResponseProcessingException e) {
				throw new ServiceException("impossible to upload the node named " + nodeR.getName());
			}

		}

		Map<LinkReader, String> linkIds = new HashMap<>();

		// TODO refactor links uploading
		for (LinkReader linkR : nffgR.getNodes().stream().flatMap(n -> n.getLinks().stream())
				.collect(Collectors.toList())) {
			Relationship relation = new Relationship();
			relation.setDstNode(nodeIds.get(linkR.getDestinationNode()));
			relation.setType("Connection");
			
			

			try {
				System.out.println("Trying to get id of node " + linkR.getSourceNode().getName());
				System.out.println(nodeIds.get(linkR.getSourceNode()));
				Relationship res = client.target(uri.toString()).path("resource").path("node")
						.path(nodeIds.get(linkR.getSourceNode()))
						.path("relationship")
						.request(MediaType.TEXT_XML).accept("*/*")
						.post(Entity.entity(relation, MediaType.APPLICATION_XML), Relationship.class);

				linkIds.put(linkR, res.getId());
				System.out.println(res.getId());
			} catch (ResponseProcessingException e) {
				throw new ServiceException("impossible to upload the node named " + linkR.getName());
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCurrentGraphName() {
		return graphName;
	}

}
