package it.polito.dp2.NFFG.sol2;

import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.*;

/**
 * Implementation of the ReachabilityTester interface. The loadNffg method
 * exploits the functionality of parallelStream
 * 
 * @author Martino Mensio
 *
 */
public class Sol2ReachabilityTester implements ReachabilityTester {

	private NffgVerifier monitor;
	private String graphName;
	private WebTarget target;
	private Map<String, String> nodeIds;

	private ObjectFactory factory = new ObjectFactory();

	public Sol2ReachabilityTester(NffgVerifier nffgR, URI uri) {
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

		// node uploading
		try {
			nodeIds = nffgR.getNodes().parallelStream().collect(Collectors.toConcurrentMap(NodeReader::getName, n -> {
				try {
					return uploadNode(n);
				} catch (ServiceException e) {
					// go out of lambda with unchecked exception
					throw new RuntimeException(e);
				}
			}));
		} catch (Exception e) {
			// transform again Exception to ServiceException
			throw new ServiceException(e);
		}

		// links uploading
		try {
			nffgR.getNodes().stream().flatMap(n -> n.getLinks().stream()).parallel().forEach(l -> {
				try {
					uploadLink(l);
				} catch (ServiceException e) {
					// go out of lambda with unchecked exception
					throw new RuntimeException(e);
				}
			});
		} catch (Exception e) {
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
			Paths res = target.path("node").path(srcId).path("paths").queryParam("dst", dstId)
					.request(MediaType.APPLICATION_XML).get(Paths.class);
			// System.out.println("found " + res.getPath().size() + "paths");
			return !res.getPath().isEmpty();
		} catch (ResponseProcessingException e) {
			throw new ServiceException(
					"impossible to process the Paths response: response processing error, status: HTTP "
							+ e.getResponse().getStatus());
		} catch (ProcessingException e) {
			throw new ServiceException("impossible to process the Paths response: processing error");
		} catch (WebApplicationException e) {
			throw new ServiceException(
					"impossible to process the Paths response: webapp error HTTP " + e.getResponse().getStatus());
		}
	}

	@Override
	public String getCurrentGraphName() {
		return graphName;
	}

	private void deleteAllNodes() throws ServiceException {
		// deletion of previous graph (nodes and links)
		try {
			Response resdel = target.path("nodes").request(MediaType.APPLICATION_XML).delete();

			if (resdel.getStatus() != 200) {
				// not successful
				throw new ServiceException("impossible to delete the previous graph: HTTP " + resdel.getStatus());
			}
		} catch (ResponseProcessingException e) {
			// e.g. if the MediaType is wrong
			throw new ServiceException("impossible to delete the previous graph because of response processing error"
					+ ", status: HTTP " + e.getResponse().getStatus());
		}
	}

	private String uploadNode(NodeReader nodeR) throws ServiceException {
		Node node = factory.createNode();
		Property nameP = factory.createProperty();
		nameP.setName("name");
		nameP.setValue(nodeR.getName());
		node.getProperty().add(nameP);
		try {
			Node res = target.path("node").request(MediaType.APPLICATION_XML)
					.post(Entity.entity(node, MediaType.APPLICATION_XML), Node.class);

			return res.getId();
		} catch (ResponseProcessingException e) {
			throw new ServiceException("response processing error with node named " + nodeR.getName()
					+ ", status: HTTP " + e.getResponse().getStatus());
		} catch (ProcessingException e) {
			throw new ServiceException("processing error with node named " + nodeR.getName());
		} catch (WebApplicationException e) {
			throw new ServiceException("webapp error on node " + nodeR.getName() + " because of error  HTTP "
					+ e.getResponse().getStatus());
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
						"error on link " + linkR.getName() + " because of error HTTP " + res.getStatus());
			}
		} catch (ResponseProcessingException e) {
			throw new ServiceException("response processing error with link named " + linkR.getName()
					+ ", status: HTTP " + e.getResponse().getStatus());
		} catch (ProcessingException e) {
			throw new ServiceException("processing error with link named " + linkR.getName());
		}
	}

}
