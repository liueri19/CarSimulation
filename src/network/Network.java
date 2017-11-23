package network;

import simulation.Car;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a neural network.
 */
public class Network {
	//separate inputs and outputs?
	private List<Node> nodes;
	private Car car;

	/**
	 * Construct a network that controls the specified car.
	 * @param car	the car to control
	 */
	public Network(Car car) {
		this.car = car;
	}

	/**
	 * Return the list of nodes in this network.
	 * @return	the list of nodes in this network
	 */
	public List getNodes() {
		return new ArrayList<>(nodes);
	}

	/**
	 * Set the nodes
	 * @param nodes
	 */
	public void setNodes(List<Node> nodes) {
		this.nodes = new ArrayList<>(nodes);
	}

	/**
	 * Ask for an action from this network.
	 */
	public void act() {

	}
}
