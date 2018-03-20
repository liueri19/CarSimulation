package network;

import java.util.Set;
import java.util.SortedSet;

/**
 * Represents a single neuron.
 */
public abstract class Node implements Comparable<Node> {
	private static long global_id = 0;
	protected final long ID;
	protected SortedSet<Connection> prevConnections;
	protected Set<Connection> nextConnections;
	private double value;

	private final String strID;
	
	public Node(SortedSet<Connection> inputs,
				Set<Connection> outputs) {
		this(getNextAvailableID(), inputs, outputs);
	}

	public Node(long id, SortedSet<Connection> inputs, Set<Connection> outputs) {
		ID = id;
		prevConnections = inputs;
		nextConnections = outputs;

		strID = buildStringID();
	}
	
	public double write(double value) {
		double oldValue = this.value;
		
		this.value = value;
		for (Connection c : nextConnections)
			c.transmit(value);
		
		return oldValue;
	}
	
	public double read() {
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
}
