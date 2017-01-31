package it.polito.dp2.NFFG;

import java.util.Set;

/**
 * An interface for accessing the data associated to an Isolation policy.
 * An isolation policy specifies a property that is the absence of paths
 * in the NF-FG that connect nodes belonging to two different sets (in
 * practice it states that the two sets of nodes are isolated from each
 * other).
 */
public interface IsolationPolicyReader extends PolicyReader {
	/**
	 * Gives the first set of nodes of this policy. 
	 * The nodes in the set belong to the NF-FG on which the policy has to be verified.
	 * @return the first set of nodes of this policy.
	 */
	Set<NodeReader> getFirstNodeSet();
	/**
	 * Gives the second set of nodes of this policy. 
	 * The nodes in the set belong to the NF-FG on which the policy has to be verified.
	 * @return the second set of nodes of this policy.
	 */
	Set<NodeReader> getSecondNodeSet();
}
