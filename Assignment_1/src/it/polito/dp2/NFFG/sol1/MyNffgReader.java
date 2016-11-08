package it.polito.dp2.NFFG.sol1;

import java.util.*;

import it.polito.dp2.NFFG.*;

public class MyNffgReader implements NffgReader {
	
	private String name;
	private Map<String,NodeReader> nodes;
	private Calendar updateTime;

	public MyNffgReader(String name, Calendar updateTime, Map<String,NodeReader> nodes) {
		this.name = name;
		this.updateTime = updateTime;
		this.nodes = nodes;
	}

	@Override
	public String getName() {
		return name;
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
