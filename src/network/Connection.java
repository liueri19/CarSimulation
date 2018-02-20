package network;

/**
 * Represents a connection between 2 neurons.
 */
public class Connection {
	private Node prevNode, nextNode;
	private double weight;
	
	public double getWeight() { return weight; }
	protected void setWeight(double weight) { this.weight = weight; }
	
	public Node getNextNode() { return nextNode; }
	protected void setNextNode(Node nextNode) { this.nextNode = nextNode; }
	
	public Node getPrevNode() { return prevNode; }
	protected void setPrevNode(Node prevNode) { this.prevNode = prevNode; }
}
