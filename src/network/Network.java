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

//	private SortedSet<Connection> connections = new TreeSet<>(connectionComparator);
	private final Set<Connection> connections = new HashSet<>();

//	/**
//	 * It doesn't really make sense to compare Connections this way, so Connection
//	 * does not implement Comparable.
//	 */
//	private static final Comparator<Connection> connectionComparator =
//			(c1, c2) -> {
//				//first compare weights, then compare prev nodes, then next nodes
//				if (c1.getWeight() != c2.getWeight())
//					return Double.compare(c1.getWeight(), c2.getWeight());
//
//				int comparePrev = c1.getPrevNode().compareTo(c2.getPrevNode());
//				if (comparePrev != 0)
//					return comparePrev;
//
//				return c1.getNextNode().compareTo(c2.getNextNode());
//			};

	
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
							connection.getWeight(),
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

		connections.add(connection);
	}


	//////////////////////////////
	//NEAT related

	/**
	 * Connects an existing Node to another existing Node, making a new Connection.
	 * This is the "add connection" mutation.
	 */
	public void addConnection(Node from, Node to, double weight) {
		from = findNode(from);
		to = findNode(to);

		if (from == null || to == null)
			throw new IllegalArgumentException("Node is not in the network");

		Connection c =
				new Connection(Connection.getNextGlobalInnovNum(), weight, from, to);

		putConnection(c);
	}

	/**
	 * Splits an existing connection into 2 new connections, inserting a new Node in
	 * between. This is the "add node" mutation.
	 * @param connection	The Connection to place the new Node on
	 */
	public void addNode(Connection connection) {
		if (!connections.contains(connection))
			throw new IllegalArgumentException("Connection is not in the network");

		connection.setEnabled(false);

		final Node newNode = new Node.NodeBuilder(NodeType.HIDDEN).build();

		final Connection connection1 =
				new Connection(
						Connection.getNextGlobalInnovNum(),
						connection.getWeight(),
						connection.getPrevNode(),
						newNode
				);
		final Connection connection2 =
				new Connection(
						Connection.getNextGlobalInnovNum(),
						1,
						newNode,
						connection.getNextNode()
				);

		newNode.addInput(connection1);
		newNode.addOutput(connection2);

		getHiddens().put(newNode.getID(), newNode);
		getConnections().add(connection1);
		getConnections().add(connection2);
	}


	//////////////////////////////
	//basic getters - nothing interesting past this point

	public SortedMap<Long, Node> getHiddens() { return hiddens; }

	public List<Node> getInputNodes() { return inputNodes; }

	public List<Node> getOutputNodes() { return outputNodes; }

	public Set<Connection> getConnections() { return connections; }
}
