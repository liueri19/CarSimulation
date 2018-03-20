package network;

/**
 * Represents a connection between 2 neurons.
 * A connection has a weight.
 */
public class Connection {
	private final Node prevNode, nextNode;
	private final double weight;

	public Connection(double weight,
					  Node prevNode,
					  Node nextNode) {
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

	/**
	 * The reverse of toString().
	 */
	public static Connection parseConnection(String s) {
		String[] components = s.split("->");

		if (components.length != 3) {
			System.err.println("Incomplete connection: " + s);
			return null;
		}

		String n0StrId = components[0];
		double weight = Double.parseDouble(components[1]);
		String n1StrId = components[2];

		Node prevNode = Node.parseNode(n0StrId);
		Node nextNode = Node.parseNode(n1StrId);

		return new Connection(weight, prevNode, nextNode);
	}

	//////////////////////////////
	//basic getters and setters

	public double getWeight() { return weight; }

	public Node getNextNode() { return nextNode; }

	public Node getPrevNode() { return prevNode; }
}
