package it.polito.dp2.NFFG.sol2;

import java.net.*;
import java.util.*;
import java.util.stream.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.*;

/**
 * Implementation of the ReachabilityTester interface. The loadNffg method
 * exploits the functionality of parallelStream in order to perform multiple
 * request at the same time. Since the network operations are orders of
 * magnitude slower than other local elaboration of data, this makes the use of
 * parallelStream a big difference. Since the parallelization is based on stream
 * API, the checked exceptions cannot be used to go out of the lambda scope, so
 * it is necessary to do some tricks to convert the checked exception to a
 * RuntimeException and viceversa.
 * 
 * @author Martino Mensio
 *
 */
public class ReachabilityTesterImpl implements ReachabilityTester {

	private NffgVerifier monitor;
	private String graphName;
	private WebTarget target;
	private Map<String, String> nodeIds;

	private ObjectFactory factory = new ObjectFactory();

	public ReachabilityTesterImpl(NffgVerifier nffgR, URI uri) {
		this.monitor = nffgR;
		graphName = null;
		target = ClientBuilder.newClient().target(uri).path("resource");
	}

	/**
	 * The main operations are (strictly ordered):
	 * <ol>
	 * <li>Delete the previous informations: calling DELETE on the nodes
	 * resource; this also removes all the links because they are nested
	 * resources inside nodes</li>
	 * <li>Upload nodes: calling POST on the node resource; these calls can be
	 * performed in parallel, and this will give benefits in terms of execution
	 * time, since the network operations are order of magnitude slower than
	 * local operations</li>
	 * <li>Upload links: calling POST on the node/{src_id}/relationship
	 * resource; these calls can also be performed in parallel, giving the same
	 * benefits as above</li>
	 * </ol>
	 */
	@Override
	public void loadNFFG(String name) throws UnknownNameException, ServiceException {
		NffgReader nffgR = monitor.getNffg(name);
		if (nffgR == null) {
			throw new UnknownNameException("no nffg with name " + name);
		}
		// clean the graphName (for failures)
		graphName = null;

		deleteAllNodes();

		// clear map of node ids (in case of failures)
		nodeIds = null;

		try {
			// node uploading (see note on the class about streams and
			// exceptions)
			nodeIds = nffgR.getNodes().parallelStream().collect(Collectors.toConcurrentMap(NodeReader::getName, n -> {
				try {
					return uploadNode(n);
				} catch (ServiceException e) {
					// go out of lambda with unchecked exception
					throw new RuntimeException(e);
				}
			}));

			// links uploading (see note on the class about streams and
			// exceptions)
			nffgR.getNodes().stream().flatMap(n -> n.getLinks().stream()).parallel().forEach(l -> {
				try {
					uploadLink(l);
				} catch (ServiceException e) {
					// go out of lambda with unchecked exception
					throw new RuntimeException(e);
				}
			});
		} catch (Exception e) {
			nodeIds = null;
			// transform again Exception to ServiceException
			throw new ServiceException(e);
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
		String srcId = (nodeIds == null) ? null : nodeIds.get(srcName);
		String dstId = (nodeIds == null) ? null : nodeIds.get(destName);

		if (srcId == null) {
			throw new UnknownNameException("no node with name " + srcName);
		}
		if (dstId == null) {
			throw new UnknownNameException("no node with name " + destName);
		}

		// request the paths from source to destination
		try {
			Response res = target.path("node").path(srcId).path("paths").queryParam("dst", dstId)
					.request(MediaType.APPLICATION_XML).get();
			if (res.getStatus() != 200) {
				throw new ServiceException("GET paths failed: expected status code 200 but it was " + res.getStatus());
			}
			return !res.readEntity(Paths.class).getPath().isEmpty();
		} catch (Exception e) {
			if (e instanceof ServiceException) {
				throw e;
			}
			throw new ServiceException("GET paths failed: " + e.getMessage());
		}
	}

	@Override
	public String getCurrentGraphName() {
		return graphName;
	}

	private void deleteAllNodes() throws ServiceException {
		// deletion of previous graph (nodes and links)
		try {
			Response res = target.path("nodes").request(MediaType.APPLICATION_XML).delete();
			if (res.getStatus() != 200) {
				throw new ServiceException(
						"DELETE nodes failed: expected status code 200 but it was " + res.getStatus());
			}
		} catch (Exception e) {
			if (e instanceof ServiceException) {
				throw e;
			}
			throw new ServiceException("DELETE node failed: " + e.getMessage());
		}
	}

	private String uploadNode(NodeReader nodeR) throws ServiceException {
		Node node = factory.createNode();
		Property nameP = factory.createProperty();
		nameP.setName("name");
		nameP.setValue(nodeR.getName());
		node.getProperty().add(nameP);
		try {
			Response res = target.path("node").request(MediaType.APPLICATION_XML)
					.post(Entity.entity(node, MediaType.APPLICATION_XML));
			if (res.getStatus() != 200) {
				throw new ServiceException("POST node failed: expected status code 200 but was " + res.getStatus());
			}
			return res.readEntity(Node.class).getId();
		} catch (Exception e) {
			if (e instanceof ServiceException) {
				throw e;
			}
			throw new ServiceException("POST node failed: " + e.getMessage());
		}
	}

	private void uploadLink(LinkReader linkR) throws ServiceException {
		Relationship relation = factory.createRelationship();
		relation.setDstNode(nodeIds.get(linkR.getDestinationNode().getName()));
		relation.setType("Link");

		try {
			Response res = target.path("node").path(nodeIds.get(linkR.getSourceNode().getName())).path("relationship")
					.request(MediaType.APPLICATION_XML).post(Entity.entity(relation, MediaType.APPLICATION_XML));

			if (res.getStatus() != 200) {
				throw new ServiceException(
						"POST relationship failed: expected status code 200 but was " + res.getStatus());
			}
		} catch (Exception e) {
			if (e instanceof ServiceException) {
				throw e;
			}
			throw new ServiceException("POST relationship failed: " + e.getMessage());
		}
	}

}
