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


	//////////////////////////////
	//basic getters

	public SortedSet<Node> getHiddens() { return hiddens; }

	public List<Node> getIns() { return ins; }

	public List<Node> getOuts() { return outs; }

	public SortedSet<Connection> getConnections() { return connections; }
}
