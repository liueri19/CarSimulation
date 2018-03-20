package network;

import java.util.*;

/**
 * Represents a single neuron.
 */
public class Node implements Comparable<Node> {
	private static long global_id = 0;
	private final long ID;
	private final List<Connection> prevConnections;
	private final List<Connection> nextConnections;
	private double value;

	private final String strID;

	public Node(long id, List<Connection> inputs, List<Connection> outputs) {
		ID = id;
		prevConnections = inputs;
		nextConnections = outputs;

		strID = buildStringID();
	}


	public static class NodeBuilder {
		private Long id;
		private List<Connection> prevConnections, nextConnections;

		public NodeBuilder() {}

		public NodeBuilder setId(long id) { this.id = id; return this; }

		public NodeBuilder setPrevConnections(List<Connection> prevConnections) {
			this.prevConnections = prevConnections;
			return this;
		}

		public NodeBuilder setNextConnections(List<Connection> nextConnections) {
			this.nextConnections = nextConnections;
			return this;
		}

		public Node build() {
			if (id == null) id = getNextAvailableID();
			if (prevConnections == null) prevConnections = new ArrayList<>();
			if (nextConnections == null) nextConnections = new ArrayList<>();

			return new Node(id, prevConnections, nextConnections);
		}
	}


	
	double write(double value) {
		double oldValue = this.value;
		
		this.value = value;
		for (Connection c : nextConnections)
			c.transmit(value);
		
		return oldValue;
	}
	
	double read() {
		return value;
	}


	public long getID() { return ID; }

	public static synchronized long getNextAvailableID() {
		return global_id++;
	}


	/**
	 * This method is used to lazily initialize the string ID. Override this method
	 * instead of the toString() method to change the string representation.
	 */
	protected String buildStringID() {
		return 'H' + Long.toHexString(getID());
	}

	@Override
	public String toString() {
		return strID;
	}


	@Override
	public int compareTo(Node node) {
		return Long.compareUnsigned(getID(), node.getID());
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Node && compareTo((Node) obj) == 0;
	}

	public List<Connection> getPrevConnections() {
		return prevConnections;
	}

	public List<Connection> getNextConnections() {
		return nextConnections;
	}


	public static Node parseNode(String strID) {
		final char type = strID.charAt(0);
		final long id = Long.parseLong(strID.substring(1), 16);
		final Node node;

		if (type == 'I')
			node = new InputNode(id, new ArrayList<>());
		else if (type == 'O')
			node = new OutputNode(id, new ArrayList<>());
		else if (type == 'H')
			node = new Node.NodeBuilder().setId(id).build();
		else
			throw new IllegalArgumentException("Invalid Node type: " + type);

		return node;
	}
}
