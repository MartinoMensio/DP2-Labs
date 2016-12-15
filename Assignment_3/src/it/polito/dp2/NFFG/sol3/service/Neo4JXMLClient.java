package it.polito.dp2.NFFG.sol3.service;

/**
 * Access to NEO4JXML service
 * 
 * @author Martino Mensio
 *
 */
public class Neo4JXMLClient {
	
	// TODO maybe add some condensed method to handle different calls to elementary calls
	
	public Node getNode(String nodeId) {
		// TODO
		return null;
	}
	
	public void addNode(Node node) {
		// TODO
	}
	
	public void deleteNode(String nodeId) {
		// TODO
	}
	
	public void deleteAllNodes() {
		// TODO
	}
	
	public void addNffgLabelToNode(String nodeId) {
		// TODO
		// NFFG needs a label "NFFG"
	}
	
	public void addBelongsToNffg(String nffgId, String nodeId) {
		// TODO
	}
	
	public void addLinkBetweenNodes(String srcNodeId, String dstNodeId) {
		// TODO
	}
	
	public void addRelationshipToNode(String nodeId, Relationship rel) {
		// TODO
		// used by addBelongs and addLink
	}
	
	public void addPropertyToNode(String nodeId, String name) {
		// TODO
		// node of NFFG or node
	}
	
	public void testReachability(String srcNodeId, String dstNodeId) {
		// TODO
	}
}
