package network;

/**
 * Represents a connection between 2 neurons.
 * A connection has a weight.
 */
public class Connection {
	private final Network network;
	private final Node prevNode, nextNode;
	private final double weight;

	public Connection(Network network,
					  double weight,
					  Node prevNode,
					  Node nextNode) {
		this.network = network;
		this.weight = weight;
		this.prevNode = prevNode;
		this.nextNode = nextNode;
	}
	
	public void transmit(double value) {
		nextNode.write(weight * value);
	}

	@Override
	public String toString() {
		return getPrevNode().toString() + "->"
				+ getWeight()  + "->"
				+ getNextNode().toString();
	}

	//////////////////////////////
	//basic getters and setters
	public Network getNetwork() { return network; }

	public double getWeight() { return weight; }

	public Node getNextNode() { return nextNode; }

	public Node getPrevNode() { return prevNode; }
}
