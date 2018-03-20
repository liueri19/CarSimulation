package network;

import java.util.*;

/**
 * Represents a neural network.
 */
public class Network {

	//set not used, order of elements must be kept
	private final List<Node> ins = new ArrayList<>();
	private List<Node> outs = new ArrayList<>();
	private SortedSet<Node> hiddens = new TreeSet<>();

	private SortedSet<Connection> connections = new TreeSet<>(connectionComparator);

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

	Node findNode(char type, long id) {
		Collection<Node> searchSpace;
		if (type == 'I')	//this could be better with an enum
			searchSpace = ins;
		else if (type == 'O')
			searchSpace = outs;
		else if (type == 'H')
			searchSpace = hiddens;
		else
			throw new IllegalArgumentException("Invalid Node type: " + type);

		for (Node node : searchSpace) {
			if (node.getID() == id)
				return node;
		}

		return null;
	}

	Node addIfAbsent(String stringID) {
		if (stringID.length() < 2)
			throw new IllegalArgumentException("Invalid string ID: " + stringID);

		char type = stringID.charAt(0);
		long id = Long.parseLong(stringID.substring(1), 16);

		Node node = findNode(type, id);

		if (node == null) {	//node not found
			if (type == 'I')
				node = new InputNode();
			else if (type == 'O')
				node = new OutputNode();
			else if (type == 'H')
				node = new Node();
			else
				throw new IllegalArgumentException("Invalid Node type: " + type);
		}

		return node;
	}


	//////////////////////////////
	//basic getters

	public SortedSet<Node> getHiddens() { return hiddens; }

	public List<Node> getIns() { return ins; }

	public List<Node> getOuts() { return outs; }

	public SortedSet<Connection> getConnections() { return connections; }
}
