package it.polito.dp2.NFFG.sol3.service.wjc;

import java.util.*;

/**
 * Access to NEO4JXML service
 * 
 * @author Martino Mensio
 *
 */
public class Neo4JXMLClient {
	
	// TODO maybe add some condensed method to handle different calls to elementary calls
	
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
	
	public void addNode(Node node) {
		// TODO
		// POST /resource/nodes
	}
	
	public void deleteNode(String nodeId) {
		// TODO
		// DELETE /resource/nodes/{nodeId}
	}
	
	public void deleteAllNodes() {
		// TODO
		// DELETE /resource/nodes
	}
	
	public void addNffgLabelToNode(String nodeId) {
		// TODO
		// NFFG needs a label "NFFG"
		// POST /resource/nodes/{nodeId}/label
	}
	
	public void addBelongsToNffg(String nffgId, String nodeId) {
		// TODO (look at addRelationshipToNode)
		// POST /resource/nodes/{nffgId}/relationship with destNode={nodeId} type="belongs"
	}
	
	public void addLinkBetweenNodes(String srcNodeId, String dstNodeId) {
		// TODO (look at addRelationshipToNode)
		// POST /resource/nodes/{srcNodeId}/relationship with destNode={dstNodeId} type="Link"
	}
	
	public void addRelationshipToNode(String nodeId, Relationship rel) {
		// TODO
		// used by addBelongs and addLink
	}
	
	public boolean testReachability(String srcNodeId, String dstNodeId) {
		// TODO
	}
}
