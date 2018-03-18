package network;

/**
 * Represents a connection between 2 neurons.
 * A connection has a weight.
 */
public class Connection {
	private Node prevNode, nextNode;
	private double weight;
	
	public double getWeight() { return weight; }
	void setWeight(double weight) { this.weight = weight; }
	
	public Node getNextNode() { return nextNode; }
	void setNextNode(Node nextNode) { this.nextNode = nextNode; }
	
	public Node getPrevNode() { return prevNode; }
	void setPrevNode(Node prevNode) { this.prevNode = prevNode; }
	
	
	public void transmit(double value) {
		nextNode.write(weight * value);
	}
}
