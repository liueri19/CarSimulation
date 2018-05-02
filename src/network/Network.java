package network;

import java.util.*;

/**
 * Represents a neural network.
 * A Network contains the genetic information of a genotype, at the same time also
 * provides the functionalities of a phenotype.
 */
public class Network {

	//order of elements must be kept
	private final List<Node> inputNodes = new ArrayList<>();
	private final List<Node> outputNodes = new ArrayList<>();
	private final SortedMap<Long, Node> hiddens = new TreeMap<>();

	private final SortedMap<Long, Connection> connections = new TreeMap<>();

	private long global_id = 0;

	private double fitness;

	public synchronized long getNextNodeID() {
		return global_id++;
	}


	/**
	 * Given a list of doubles as input values for the input nodes, computes through the
	 * network and returns a list of doubles containing the results from the output nodes.
	 */
	public List<Double> compute(List<Double> inputs) {
		for (int i = 0; i < inputNodes.size(); i++) {
			Node node = inputNodes.get(i);
			double value =
					i < inputs.size() ? inputs.get(i) : 0;

			node.write(value);
		}
		
		List<Double> outputs = new ArrayList<>();
		for (Node out : outputNodes)
			outputs.add(out.read());

		return outputs;
	}


	Node findNode(Node node) {
		List<Node> searchSpace = null;

		if (node.getNodeType() == NodeType.INPUT)
			searchSpace = inputNodes;
		else if (node.getNodeType() == NodeType.OUTPUT)
			searchSpace = outputNodes;

		if (searchSpace != null) {	//linear search for list
			for (Node n : searchSpace) {
				if (n.getID() == node.getID())
					return n;
			}
			return null;	//not found
		}

		else {
			return hiddens.get(node.getID());
		}
	}


	/**
	 * Puts the specified Node into this network. Returns true if the operation
	 * succeeded. If this network already contains the specified Node, the operation fails
	 * and returns false.
	 * Note that this is not the "add node" mutation.
	 */
	public boolean putNode(Node node) {
		NodeType nodeType = node.getNodeType();

		if (nodeType == NodeType.INPUT && !inputNodes.contains(node))	//don't add duplicates
			return inputNodes.add(node);

		else if (nodeType == NodeType.OUTPUT && !outputNodes.contains(node))
			return outputNodes.add(node);

		else if (nodeType == NodeType.HIDDEN && !hiddens.containsKey(node.getID())) {
			hiddens.put(node.getID(), node);
			return true;
		}

		return false;
	}

	/**
	 * Puts the specified Connection into this network.
	 * Note that this is not the "add connection" mutation.
	 */
	public void putConnection(Connection connection) {
		Node[] endNodes =
				new Node[] { connection.getPrevNode(), connection.getNextNode() };

		boolean nodeChanged = false;
		for (int i = 0; i < endNodes.length; i++) {
			Node node = endNodes[i];

			boolean kept;
			//try add the node to network;
			// if succeed, keep existing reference, otherwise find the reference in the network.
			endNodes[i] = (kept = putNode(node)) ? node : findNode(node);

			if (!kept)
				nodeChanged = true;
		}

		if (nodeChanged) {	//need to modify connection
			connection =
					new Connection(
							connection.getInnovationNumber(),
							connection.getWeight(), connection.getBias(),
							endNodes[0], endNodes[1]
					);
		}


		final List<Connection> prevNodeConnections =
				connection.getPrevNode().getNextConnections();
		if (!prevNodeConnections.contains(connection))
			connection.getPrevNode().addOutput(connection);

		final List<Connection> nextNodeConnections =
				connection.getNextNode().getPrevConnections();
		if (!nextNodeConnections.contains(connection))
			connection.getNextNode().addInput(connection);

		connections.put(connection.getInnovationNumber(), connection);
	}


	//////////////////////////////
	//NEAT related

	/**
	 * Connects an existing Node to another existing Node, making a new Connection.
	 * This is the "add connection" mutation.
	 */
	public void addConnection(Node from, Node to, double weight, double bias) {
		from = findNode(from);
		to = findNode(to);

		if (from == null || to == null)
			throw new IllegalArgumentException("Node is not in the network");

		Connection c =
				new Connection(Connection.getNextGlobalInnovNum(), weight, bias, from, to);

		putConnection(c);
	}

	/**
	 * Splits an existing connection into 2 new connections, inserting a new Node in
	 * between. This is the "add node" mutation.
	 * @param connection	The Connection to place the new Node on
	 */
	public void addNode(Connection connection) {
		if (!connections.containsKey(connection.getInnovationNumber()))
			throw new IllegalArgumentException("Connection is not in the network");

		connection.setEnabled(false);

		final Node newNode = new Node.NodeBuilder(NodeType.HIDDEN, getNextNodeID()).build();

		// keeps identical weight and bias
		final Connection connection1 =
				new Connection(
						Connection.getNextGlobalInnovNum(),
						connection.getWeight(), connection.getBias(),
						connection.getPrevNode(),
						newNode
				);
		// weight of 1 and bias of 0
		final Connection connection2 =
				new Connection(
						Connection.getNextGlobalInnovNum(),
						1, 0,
						newNode,
						connection.getNextNode()
				);

		newNode.addInput(connection1);
		newNode.addOutput(connection2);

		getHiddens().put(newNode.getID(), newNode);
		getConnections().put(connection1.getInnovationNumber(), connection1);
		getConnections().put(connection2.getInnovationNumber(), connection2);
	}


	/**
	 * Performs a crossover with the specified Network and returns the offspring.
	 */
	public Network reproduceWith(Network other) {
		// TODO crossover


		return null;
	}

	private static final Random RANDOM = new Random();
	private static boolean randomBoolean() {
		return RANDOM.nextBoolean();
	}


	//////////////////////////////
	//basic getters - nothing interesting past this point

	public Map<Long, Node> getHiddens() { return hiddens; }
	public List<Node> getInputNodes() { return inputNodes; }
	public List<Node> getOutputNodes() { return outputNodes; }
	public Map<Long, Connection> getConnections() { return connections; }
	public double getFitness() { return fitness; }
	public void setFitness(double fitness) { this.fitness = fitness; }
}
