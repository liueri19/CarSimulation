package network;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single neuron.
 */
public class Node implements Comparable<Node> {
	private final long ID;
	private final String strID;
	private final NodeType type;
	private final List<Connection> prevConnections;
	private final List<Connection> nextConnections;

	private final List<Double> oldValues = new ArrayList<>();
	private double value;

	/**
	 * Returns a new Node instance with only a type and id.
	 * Node objects constructed from this method is not functional, they may only be used
	 * as a data holder. Attempting to perform operations on such objects results in
	 * undefined behavior.
	 */
	public static Node getDataHolder(NodeType type, long id) {
		return new Node(type, id, null, null);
	}

	private Node(NodeType type, long id, List<Connection> inputs, List<Connection> outputs) {
		this.type = type;
		ID = id;
		prevConnections = inputs;
		nextConnections = outputs;

		strID = initStringID();
	}


	public static class NodeBuilder {
		private long id;
		private NodeType type;
		private List<Connection> prevConnections, nextConnections;

		public NodeBuilder(NodeType nodeType, long id) {
			type = nodeType;
			this.id = id;
		}

		public NodeBuilder setNodeType(NodeType type) { this.type = type; return this; }

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
			if (type == null) type = NodeType.HIDDEN;
			if (prevConnections == null) prevConnections = new ArrayList<>();
			if (nextConnections == null) nextConnections = new ArrayList<>();

			return new Node(type, id, prevConnections, nextConnections);
		}
	}


	
	void write(double value) {
		oldValues.add(value);

		//update value if all previous connections have transmitted
		if (oldValues.size() >= prevConnections.size()) {
			double sum = 0;
			for (double d : oldValues)
				sum += d;

			this.value = sum / oldValues.size();

			oldValues.clear();
		}

		for (Connection c : nextConnections)
			c.transmit(value);
	}
	
	double read() {
		return value;
	}


	public static Node parseNode(String strID) {
		if (strID == null) throw new NullPointerException();
		if (strID.length() < 2)
			throw new IllegalArgumentException("Incomplete Node: " + strID);

		final char typeChar = strID.charAt(0);
		final long id = Long.parseLong(strID.substring(1), 16);

		final NodeType type = NodeType.of(String.valueOf(typeChar));
		final NodeBuilder builder =
				new Node.NodeBuilder(type, id);

		return builder.build();
	}


	/**
	 * This method is used to lazily initialize the string ID. Override this method
	 * instead of the toString() method to change the string representation.
	 */
	protected String initStringID() {
		return type.toString() + Long.toHexString(getID());
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


	public long getID() { return ID; }


	public boolean addInput(Connection connection) {
		return prevConnections.add(connection);
	}

	public boolean addOutput(Connection connection) {
		return nextConnections.add(connection);
	}

	//here goes the boring getters
	public NodeType getNodeType() { return type; }

	public List<Connection> getPrevConnections() {
		return prevConnections;
	}

	public List<Connection> getNextConnections() {
		return nextConnections;
	}
}
