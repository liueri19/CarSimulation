package network;

import java.util.List;

/**
 * Represents a single neuron.
 */
public abstract class Node {
	private static long global_id = 0;
	protected final long ID;
	protected List<Connection> prevConnections, nextConnections;
	private double value;
	
	public Node(List<Connection> inputs,
				List<Connection> outputs) {
		this(getNextAvailableID(), inputs, outputs);
	}

	public Node(long id, List<Connection> inputs, List<Connection> outputs) {
		ID = id;
		prevConnections = inputs;
		nextConnections = outputs;
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
}
