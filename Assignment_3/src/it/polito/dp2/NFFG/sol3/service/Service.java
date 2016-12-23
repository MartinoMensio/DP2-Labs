package it.polito.dp2.NFFG.sol3.service;

import java.net.*;
import java.util.*;

import it.polito.dp2.NFFG.lab3.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;
import it.polito.dp2.NFFG.sol3.service.wjc.*;

/**
 * Core functionality: orchestrator + response builder. Called by the web
 * interface, this class interacts with the persistence and the Neo4JXML client
 * 
 * @author Martino Mensio
 *
 */
public class Service {

	private Neo4JXMLClient neoClient;

	// retrieve data from persistence
	private Persistence data;

	private Service(URI neo4jLocation) {
		neoClient = new Neo4JXMLClient(neo4jLocation);
		data = Persistence.getPersistence();
	}

	public static Service createService() throws ServiceException {
		String url = System.getProperty("it.polito.dp2.NFFG.lab3.NEO4JURL");
		if (url == null) {
			url = "http://localhost:8080/Neo4JXML/rest";
		}
		try {
			return new Service(URI.create(url));
		} catch (IllegalArgumentException e) {
			throw new ServiceException(e);
		}
	}

	public List<NffgT> getNffgs() {
		List<Node> neoNodes = neoClient.getNodes();
		List<NffgT> nffgs = new ArrayList<>();
		for(Node neoNode : neoNodes) {
			if(neoNode.getLabels().getValue().contains("NFFG")) {
				// this is an nffg
				// TODO use factory
				NffgT nffg = new NffgT();
				nffg.setName(neoNode.getProperty().stream().filter(p -> p.getName().equals("name")).findFirst().get().getValue());
				// TODO check note some lines after (about nodes processing
				nffgs.add(nffg);
			}
		}
		for(Node neoNode : neoNodes) {
			if(!neoNode.getLabels().getValue().contains("NFFG")) {
				// this is not an NFFG
				List<Node> neoNodesForThisNffg = new ArrayList<>();
				// test reachability to understand if this node belongs to some nffg
				// TODO looping: nodes,nffg could be done also inside the previous loop, but maybe this can be made parallelized
				for(NffgT nffg : nffgs) {
					if(neoClient.testReachability(data.nodesId.get(nffg.getName()), data.nodesId.get(neoNode.getProperty().stream().filter(p -> p.getName().equals("name")).findFirst().get().getValue()))) {
						// this node belongs to this nffg
						// TODO create NodeT
						//nffg.getNode().add(e)
					}
				}
			}
		}
		return nffgs;
	}

	public NffgT getNffg(String name) {
		// TODO
		return null;
	}
	
	public NffgT storeNffg(NffgT nffg) {
		
		// Add the nffg fake node to neo4j
		Node nffgN = addNamedNode(nffg.getName());
		neoClient.addNffgLabelToNode(nffgN.getId());
		
		// adding all the nodes to neo4j
		for(NodeT node : nffg.getNode()) {
			Node nodeN = addNamedNode(node.getName());
			neoClient.addBelongsToNffg(nffgN.getId(), nodeN.getId());
		}
		
		// store the Nffg in the persistence
		data.nffgsMap.put(nffg.getName(), nffg);
		return nffg;
	}
	
	Node addNamedNode(String nodeName) {
		Node nodeReq = new Node();
		Property nameProp = new Property();
		nameProp.setName("name");
		nameProp.setValue(nodeName);
		nodeReq.getProperty().add(nameProp);
		// TODO exceptions
		Node nodeRes = neoClient.addNode(nodeReq);
		return nodeRes;
	}
}
