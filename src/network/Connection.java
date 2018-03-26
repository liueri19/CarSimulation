package network;

/**
 * Represents a connection between 2 neurons.
 * A connection has a weight and an innovation number.
 */
public class Connection {
	private final Node prevNode, nextNode;
	private double weight, bias;
	private final long innovNum;
	private boolean enabled = true;

	private static long globalInnovationNumber = 0;

	public Connection(long innovationNumber,
					  double weight, double bias,
					  Node prevNode,
					  Node nextNode) {
		innovNum = innovationNumber;
		this.weight = weight;
		this.bias = bias;
		this.prevNode = prevNode;
		this.nextNode = nextNode;
	}
	
	public void transmit(double value) {
		if (isEnabled())
			getNextNode().write(getWeight() * value);
	}

	@Override
	public String toString() {
		return getInnovationNumber() + ":\t" +
				getPrevNode().toString() + "->" +
				getWeight()  + "+" + getBias() + "->" +
				getNextNode().toString();
	}

	/**
	 * The reverse of toString(). Take a String representation of Connection and returns
	 * a new Connection instance with corresponding data.
	 */
	public static Connection parseConnection(String s) {
		/* innovNum:	N_id0->weight+bias->N_id1 */
		String[] components = s.split("->");
		/* innovNum:	N_id0, weight+bias, N_id1 */

		if (components.length != 3)
			throw new IllegalArgumentException("Incomplete connection entry: " + s);

		String[] innovNumAndStrID = components[0].split(":\t");
		/* [innovNum, N_id0], weight+bias, N_id1 */
		String[] weightAndBias = components[1].split("\\+");
		/* [innovNum, N_id0], [weight, bias], N_id1 */

		if (innovNumAndStrID.length != 2 || weightAndBias.length != 2)
			throw new IllegalArgumentException("Incomplete connection entry: " + s);

		long innovNum = Long.parseLong(innovNumAndStrID[0]);
		String n0StrId = innovNumAndStrID[1];

		double weight = Double.parseDouble(weightAndBias[0]);
		double bias = Double.parseDouble(weightAndBias[1]);

		String n1StrId = components[2];

		Node prevNode = Node.parseNode(n0StrId);
		Node nextNode = Node.parseNode(n1StrId);

		return new Connection(innovNum, weight, bias, prevNode, nextNode);
	}


	/**
	 * Two Connections are logically equal if they both connect the same Nodes, even if
	 * they have different weights, bias, or innovation numbers.
	 */
	@Override
	public boolean equals(Object c) {
		return c instanceof Connection &&
				getPrevNode().equals(((Connection) c).getPrevNode()) &&
				getNextNode().equals(((Connection) c).getNextNode());
	}


	static long getNextGlobalInnovNum() {
		synchronized (Connection.class) {
			return globalInnovationNumber++;
		}
	}

	//////////////////////////////
	//basic getters and setters
	public long getInnovationNumber() { return innovNum; }

	public double getWeight() { return weight; }
	public void setWeight(double weight) { this.weight = weight; }
	public double getBias() { return bias; }
	public void setBias(double bias) { this.bias = bias; }

	public Node getNextNode() { return nextNode; }

	public Node getPrevNode() { return prevNode; }

	public boolean isEnabled() { return enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
