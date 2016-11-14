package it.polito.dp2.NFFG.sol1;

import java.util.*;

import it.polito.dp2.NFFG.*;

public class MyNffgReader extends MyNamedEntityReader implements NffgReader {

	private Map<String,NodeReader> nodes;
	private Calendar updateTime;

	public MyNffgReader(String name, Calendar updateTime) {
		super(name);
		this.updateTime = updateTime;
		this.nodes = new HashMap<>();
	}
	
	void addNode(NodeReader node) {
		nodes.put(node.getName(), node);
	}
	
	MyNodeReader getMyNode(String nodeName) {
		NodeReader tmp = nodes.get(nodeName);
		if(tmp != null && tmp instanceof MyNodeReader) {
			return (MyNodeReader)tmp;
		}
		return null;
	}

	@Override
	public NodeReader getNode(String nodeName) {
		return nodes.get(nodeName);
	}

	@Override
	public Set<NodeReader> getNodes() {
		return new HashSet<>(nodes.values());
	}

	@Override
	public Calendar getUpdateTime() {
		return updateTime;
	}

}
