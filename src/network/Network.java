package network;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a neural network.
 */
public class Network {
	private List<Node> ins, outs, hiddens;

	public List<Node> getHiddens() { return new ArrayList<>(hiddens); }
	protected void setHiddens(List<Node> hiddens) { this.hiddens = new ArrayList<>(hiddens); }
	
	public List<Node> getIns() { return ins; }
	public void setIns(List<Node> ins) { this.ins = ins; }
	
	public List<Node> getOuts() { return outs; }
	public void setOuts(List<Node> outs) { this.outs = outs; }
	
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
}
