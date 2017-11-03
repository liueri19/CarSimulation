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
	private final Car car;

	//constructor should load network from file
	public Network(Car car) {
		this.car = car;
	}

	/**
	 * Ask for an action from this network.
	 */
	public void act() {

	}

	//a method should write current configuration to file
}
