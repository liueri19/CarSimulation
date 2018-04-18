package ga;

import network.Network;
import simulation.Simulation;
import utils.MapIO;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class CarControlEvaluator implements Evaluator {
	private final List<Line2D> map = new ArrayList<>();
	private final boolean doGraphics;

	public CarControlEvaluator(String mapFile, boolean doGraphics) {
		map.addAll(MapIO.readMapSilently(mapFile));
		this.doGraphics = doGraphics;
	}


	@Override
	public double evaluate(Network network) {
		Simulation.runSimulation(map, network, doGraphics);

		final double percentCompleted = 0;
		final long timeConsumed = 0;

		// TODO calculate fitness
		double fitness = 0;

		return fitness;
	}
}
