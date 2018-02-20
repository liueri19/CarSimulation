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
	 * Compute from the inputs.
	 */
	public List<Double> compute(List<Double> inputs) {
		int numNodes, inputSize;
		numNodes = ins.size();
		inputSize = inputs.size();
		int bound = inputSize > numNodes ? numNodes : inputSize;	//lower of the 2
		List<Double> outputs = new ArrayList<>();
		
		for (int i = 0; i < bound; i++) {
			Node n = ins.get(i);
			double d = inputs.get(i);
			//write value 'd' to 'n'
			//add to outputs
		}
		
		return outputs;
	}
}
