package it.polito.dp2.NFFG.sol3.service;

import java.util.*;

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

	private Neo4JXMLClient neoClient = new Neo4JXMLClient();

	// retrieve data from persistence
	private Persistence data = Persistence.getPersistence();

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
						nffg.getNode().add(e)
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
}
