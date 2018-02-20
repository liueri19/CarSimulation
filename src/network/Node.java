package network;

import java.util.List;

/**
 * Represents a single neuron.
 */
public abstract class Node {
	private List<List<Connection>> prevConnections, nextConnections;
}
