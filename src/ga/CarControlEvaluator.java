package ga;

import network.Network;
import simulation.SimulationMain;
import utils.MapIO;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class CarControlEvaluator implements Evaluator {
	private final List<Line2D> map = new ArrayList<>();

	public CarControlEvaluator(String mapFile) {
		map.addAll(MapIO.readMapSilently(mapFile));
	}


	@Override
	public double evaluate(Network network) {
		SimulationMain.runSimulation(map, network, true);

		final double percentCompleted = 0;
		final long timeConsumed = 0;

		// TODO calculate fitness
		final double fitness = 0;

		return fitness;
	}
}
