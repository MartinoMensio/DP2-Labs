package it.polito.dp2.NFFG.sol3.service.wjc;

import java.net.*;
import java.util.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

/**
 * Access to NEO4JXML service
 * 
 * @author Martino Mensio
 *
 */
public class Neo4JXMLClient {

	private WebTarget target;

	// TODO maybe add some condensed method to handle different calls to
	// elementary calls

	public Neo4JXMLClient(URI neo4jLocation) {
		// TODO Auto-generated constructor stub
		target = ClientBuilder.newClient().target(neo4jLocation).path("resource");
	}

	public List<Node> getNodes() {
		// TODO
		// GET /resource/nodes
		return null;
	}

	public Node getNode(String nodeId) {
		// TODO
		// GET /resource/nodes/{nodeId}
		return null;
	}

	public Node addNode(Node node) {
		// TODO
		// POST /resource/node
		Node result = target.path("node").request(MediaType.APPLICATION_XML)
				.post(Entity.entity(node, MediaType.APPLICATION_XML), Node.class);
		// TODO check exceptions
		return result;
	}

	public void deleteNode(String nodeId) {
		// TODO
		// DELETE /resource/nodes/{nodeId}
	}

	public void deleteAllNodes() {
		// TODO
		// DELETE /resource/nodes
		try {
			Response resdel = target.path("nodes").request(MediaType.APPLICATION_XML).delete();

			if (resdel.getStatus() != 200) {
				// not successful
				throw new RuntimeException("impossible to clear neo4j: HTTP " + resdel.getStatus());
			}
		} catch (ResponseProcessingException e) {
			// e.g. if the MediaType is wrong
			throw new RuntimeException("impossible to clear neo4j because of response processing error"
					+ ", status: HTTP " + e.getResponse().getStatus());
		}
	}

	public void addNffgLabelToNode(String nodeId) {
		// TODO
		// NFFG needs a label "NFFG"
		// POST /resource/nodes/{nodeId}/label
		// TODO use factory
		Labels label = new Labels();
		label.getValue().add("NFFG");
		Response res = target.path("node").path(nodeId).path("label").request(MediaType.APPLICATION_XML)
				.post(Entity.entity(label, MediaType.APPLICATION_XML));
		if (res.getStatus() != 204) {
			// TODO change type of exception
			throw new RuntimeException("LABEL");
		}
	}

	public Relationship addBelongsToNffg(String nffgId, String nodeId) {
		// TODO (look at addRelationshipToNode)
		// POST /resource/nodes/{nffgId}/relationship with destNode={nodeId}
		// type="belongs"
		Relationship rel = new Relationship();
		rel.setDstNode(nodeId);
		rel.setType("belongs");
		return addRelationshipToNode(nffgId, rel);
	}

	public Relationship addLinkBetweenNodes(String srcNodeId, String dstNodeId) {
		// TODO (look at addRelationshipToNode)
		// POST /resource/nodes/{srcNodeId}/relationship with
		// destNode={dstNodeId} type="Link"
		Relationship rel = new Relationship();
		rel.setDstNode(dstNodeId);
		rel.setType("Link");
		return addRelationshipToNode(srcNodeId, rel);
	}

	public Relationship addRelationshipToNode(String nodeId, Relationship rel) {
		// TODO
		// used by addBelongs and addLink
		Relationship result = target.path("node").path(nodeId).path("relationship").request(MediaType.APPLICATION_XML)
				.post(Entity.entity(rel, MediaType.APPLICATION_XML), Relationship.class);
		// TODO check exceptions
		return result;
	}

	public boolean testReachability(String srcNodeId, String dstNodeId) {
		// TODO
		Paths res = target.path("node").path(srcNodeId).path("paths").queryParam("dst", dstNodeId)
				.request(MediaType.APPLICATION_XML).get(Paths.class);
		// TODO exceptions
		return !res.getPath().isEmpty();
	}
}
