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
		Simulation.Result r = Simulation.runSimulation(map, network, doGraphics);

		final double completion = r.getCompletion();
		final long operations = r.getOperations();

			/*
			The more operations the network took, the slower it drove, the lower the score.
			The more the network completes, the higher the score.

			Completion is squared so it is more important.
			 */
		double fitness = completion * completion / operations;
		return fitness;
	}
}
