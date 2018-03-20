package network;

import java.util.*;

/**
 * Represents a neural network.
 */
public class Network {

	//order of elements must be kept
	private final List<Node> ins = new ArrayList<>();
	private final List<Node> outs = new ArrayList<>();
	private final SortedMap<Long, Node> hiddens = new TreeMap<>();

//	private SortedSet<Connection> connections = new TreeSet<>(connectionComparator);
	private final Set<Connection> connections = new HashSet<>();

	/**
	 * It doesn't really make sense to compare Connections this way, so Connection
	 * does not implement Comparable.
	 */
	private static final Comparator<Connection> connectionComparator =
			(c1, c2) -> {
				//first compare weights, then compare prev nodes, then next nodes
				if (c1.getWeight() != c2.getWeight())
					return Double.compare(c1.getWeight(), c2.getWeight());

				int comparePrev = c1.getPrevNode().compareTo(c2.getPrevNode());
				if (comparePrev != 0)
					return comparePrev;

				return c1.getNextNode().compareTo(c2.getNextNode());
			};

	
	/**
	 * Given a list of doubles as input values for the input nodes, computes through the
	 * network and returns a list of doubles containing the results from the output nodes.
	 */
	public List<Double> compute(List<Double> inputs) {
		for (int i = 0; i < ins.size(); i++) {
			Node node = ins.get(i);
			double value =
					i < inputs.size() ? inputs.get(i) : 0;

			node.write(value);
		}
		
		List<Double> outputs = new ArrayList<>();
		for (Node out : outs)
			outputs.add(out.read());

		return outputs;
	}



//	/**
//	 * Looks for the Node with the specified id. Returns the matching Node if found,
//	 * otherwise adds a new isolated Node with the specified id to the network and returns
//	 * the new Node.
//	 */
//	Node addIfAbsent(String stringID) {
//		if (stringID.length() < 2)
//			throw new IllegalArgumentException("Invalid string ID: " + stringID);
//
//		char type = stringID.charAt(0);
//		long id = Long.parseLong(stringID.substring(1), 16);
//
//		Node node = findNode(type, id);
//
//		if (node == null)	//node not found
//			node = Node.parseNode(stringID);
//
//		addNode(node);
//
//		return node;
//	}


	Node findNode(Node node) {
		List<Node> searchSpace = null;

		if (node instanceof InputNode)
			searchSpace = ins;
		else if (node instanceof OutputNode)
			searchSpace = outs;

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
	 * Adds the specified Node to this network. Returns true if the operation succeeded.
	 */
	boolean addNode(Node node) {
		if (node instanceof InputNode && !ins.contains(node))	//don't add duplicates
			return ins.add(node);
		else if (node instanceof OutputNode && !outs.contains(node))
			return outs.add(node);
		else if (!hiddens.containsKey(node.getID())) {
			hiddens.put(node.getID(), node);
			return true;
		}

		return false;
	}

	void addConnection(Connection connection) {
		Node[] endNodes =
				new Node[] { connection.getPrevNode(), connection.getNextNode() };

		for (int i = 0; i < endNodes.length; i++) {
			Node node = endNodes[i];

			//try add the node to network;
			// if succeed, keep existing reference, otherwise find the node reference in the network.
			node = addNode(node) ? node : findNode(node);

			List<Connection> nodeConnections;
			if (i == 0)
				nodeConnections = node.getNextConnections();
			else
				nodeConnections = node.getPrevConnections();

			if (!nodeConnections.contains(connection))
				nodeConnections.add(connection);
		}
	}


	//////////////////////////////
	//basic getters

	public SortedMap<Long, Node> getHiddens() { return hiddens; }

	public List<Node> getIns() { return ins; }

	public List<Node> getOuts() { return outs; }

	public Set<Connection> getConnections() { return connections; }
}
