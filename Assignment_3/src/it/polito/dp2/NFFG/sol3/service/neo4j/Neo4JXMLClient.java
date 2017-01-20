package it.polito.dp2.NFFG.sol3.service.neo4j;

import java.net.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

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
		Node result = target.path("node").request(MediaType.APPLICATION_XML)
				.post(Entity.entity(node, MediaType.APPLICATION_XML), Node.class);
		// TODO check exceptions
		return result;
	}

	/**
	 * DELETE /resource/nodes
	 */
	public void deleteAllNodes() {
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

	/**
	 * POST /resource/nodes/{nodeId}/label with NFFG label
	 * 
	 * @param nodeId
	 */
	public void addNffgLabelToNode(String nodeId) {
		Labels label = factory.createLabels();
		// NFFG needs a label "NFFG"
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
		Relationship result = target.path("node").path(nodeId).path("relationship").request(MediaType.APPLICATION_XML)
				.post(Entity.entity(rel, MediaType.APPLICATION_XML), Relationship.class);
		// TODO check exceptions
		return result;
	}

	/**
	 * GET /resource/node/{srcNodeId}/paths?dst={dstNodeId}
	 * 
	 * @param srcNodeId
	 * @param dstNodeId
	 * @return
	 */
	public boolean testReachability(String srcNodeId, String dstNodeId) {
		Paths res = target.path("node").path(srcNodeId).path("paths").queryParam("dst", dstNodeId)
				.request(MediaType.APPLICATION_XML).get(Paths.class);
		// TODO exceptions
		return !res.getPath().isEmpty();
	}

	public String addNamedNode(String nodeName) {
		Node nodeReq = factory.createNode();
		Property nameProp = factory.createProperty();
		nameProp.setName("name");
		nameProp.setValue(nodeName);
		nodeReq.getProperty().add(nameProp);
		// TODO exceptions
		Node nodeRes = addNode(nodeReq);
		return nodeRes.getId();
	}
}
