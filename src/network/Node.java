package network;

import java.util.List;

/**
 * Represents a single neuron.
 */
public abstract class Node {
	private List<Connection> prevConnections, nextConnections;
	private double value;
	
	public Node() {
	
	}
	
	double write(double value) {
		double oldValue = this.value;
		
		this.value = value;
		for (Connection c : nextConnections)
			c.transmit(value);
		
		return oldValue;
	}
	
	public double read() {
		return value;
	}
}
