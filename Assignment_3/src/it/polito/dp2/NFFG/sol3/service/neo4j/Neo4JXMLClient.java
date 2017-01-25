package it.polito.dp2.NFFG.sol3.service.neo4j;

import java.net.*;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;

/**
 * Access to NEO4JXML service
 * 
 * @author Martino Mensio
 *
 */
public class Neo4JXMLClient {

	private static ObjectFactory factory = new ObjectFactory();
	private WebTarget target;

	public Neo4JXMLClient(URI neo4jLocation) {
		target = ClientBuilder.newClient().target(neo4jLocation).path("resource");
	}

	/**
	 * POST /resource/node
	 * 
	 * @param node
	 * @return
	 */
	public Node addNode(Node node) {
		try {
			Response res = target.path("node").request(MediaType.APPLICATION_XML)
					.post(Entity.entity(node, MediaType.APPLICATION_XML));
			if (res.getStatus() != 200) {
				throw new NeoFailedException(
						"POST node failed: expected status code 200 but it was " + res.getStatus());
			}
			return res.readEntity(Node.class);
		} catch (Exception e) {
			if (e instanceof NeoFailedException) {
				throw e;
			}
			throw new NeoFailedException("POST node failed: " + e.getMessage());
		}
	}

	/**
	 * DELETE /resource/nodes
	 */
	public void deleteAllNodes() {
		try {
			Response res = target.path("nodes").request(MediaType.APPLICATION_XML).delete();
			if (res.getStatus() != 200) {
				throw new NeoFailedException(
						"DELETE nodes failed: expected status code 200 but it was " + res.getStatus());
			}
		} catch (Exception e) {
			if (e instanceof NeoFailedException) {
				throw e;
			}
			throw new NeoFailedException("DELETE nodes failed: " + e.getMessage());
		}
	}

	/**
	 * POST /resource/nodes/{nodeId}/label with NFFG label
	 * 
	 * @param nodeId
	 */
	public void addNffgLabelToNode(String nodeId) {
		Labels label = factory.createLabels();
		// NFFG needs a label "NFFG"
		label.getValue().add("NFFG");
		try {
			Response res = target.path("node").path(nodeId).path("label").request(MediaType.APPLICATION_XML)
					.post(Entity.entity(label, MediaType.APPLICATION_XML));
			if (res.getStatus() != 204) {
				throw new NeoFailedException(
						"POST label failed: expected status code 204 but it was " + res.getStatus());
			}
		} catch (Exception e) {
			if (e instanceof NeoFailedException) {
				throw e;
			}
			throw new NeoFailedException("POST label failed: " + e.getMessage());
		}
	}

	/**
	 * Creates a relationship with DestNode=nodeId type="belongs". Then the
	 * relationship is added to the nffg node
	 * 
	 * @param nffgId
	 * @param nodeId
	 * @return
	 */
	public Relationship addBelongsToNffg(String nffgId, String nodeId) {
		Relationship rel = factory.createRelationship();
		rel.setDstNode(nodeId);
		rel.setType("belongs");
		return addRelationshipToNode(nffgId, rel);
	}

	/**
	 * Creates a relationship with destNode=dstNodeId type="Link". Then the
	 * relationship is added to the source node
	 * 
	 * @param srcNodeId
	 * @param dstNodeId
	 * @return
	 */
	public Relationship addLinkBetweenNodes(String srcNodeId, String dstNodeId) {
		Relationship rel = factory.createRelationship();
		rel.setDstNode(dstNodeId);
		rel.setType("Link");
		return addRelationshipToNode(srcNodeId, rel);
	}

	/**
	 * POST /resource/nodes/{nodeId}/relationship used by addBelongs and addLink
	 * 
	 * @param nodeId
	 * @param rel
	 * @return
	 */
	public Relationship addRelationshipToNode(String nodeId, Relationship rel) {
		try {
			Response res = target.path("node").path(nodeId).path("relationship").request(MediaType.APPLICATION_XML)
					.post(Entity.entity(rel, MediaType.APPLICATION_XML));
			if (res.getStatus() != 200) {
				throw new NeoFailedException(
						"POST relationship failed: expected status code 200 but it was " + res.getStatus());
			}
			return res.readEntity(Relationship.class);
		} catch (Exception e) {
			if (e instanceof NeoFailedException) {
				throw e;
			}
			throw new NeoFailedException("POST relationship failed: " + e.getMessage());
		}
	}

	/**
	 * GET /resource/node/{srcNodeId}/paths?dst={dstNodeId}
	 * 
	 * @param srcNodeId
	 * @param dstNodeId
	 * @return
	 */
	public boolean testReachability(String srcNodeId, String dstNodeId) {
		try {
			Response res = target.path("node").path(srcNodeId).path("paths").queryParam("dst", dstNodeId)
					.request(MediaType.APPLICATION_XML).get();
			if (res.getStatus() != 200) {
				throw new NeoFailedException(
						"GET paths failed: expected status code 200 but it was " + res.getStatus());
			}
			return !res.readEntity(Paths.class).getPath().isEmpty();
		} catch (Exception e) {
			if (e instanceof NeoFailedException) {
				throw e;
			}
			throw new NeoFailedException("GET paths failed: " + e.getMessage());
		}
	}

	public String addNamedNode(String nodeName) {
		Node nodeReq = factory.createNode();
		Property nameProp = factory.createProperty();
		nameProp.setName("name");
		nameProp.setValue(nodeName);
		nodeReq.getProperty().add(nameProp);
		Node nodeRes = addNode(nodeReq);
		return nodeRes.getId();
	}
}
