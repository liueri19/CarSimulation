package simulation;

import network.Network;
import utils.MapIO;
import utils.NetworkIO;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Simulation {
	public static final long UPDATE_INTERVAL = 10;	//ms

	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	
	public static void main(String[] args) {
		/*
		2 arguments, both optional:
		map
		network
		 */
		final List<Line2D> edges;
		if (args.length >= 1)
			edges = MapIO.readMapSilently(args[0]);
		else
			edges = Collections.emptyList();

		final Network network;
		if (args.length >= 2)
			network = NetworkIO.readSilently(args[1]);
		else
			network = null;

		runSimulation(edges, network, true);

		shutdown();

		System.exit(0);
	}


	/**
	 * A class that bundles information about the result of a simulation run.
	 */
	public static class Result {
		private long operationsConsumed;
		private double completion;

		Result() {}
		Result(long operations, double completion) {
			operationsConsumed = operations;
			this.completion = completion;
		}

		public synchronized long getOperations() { return operationsConsumed; }
		public synchronized double getCompletion() { return completion; }

		public synchronized void setCompletion(double completion) { this.completion = completion; }
		public synchronized void setOperations(long operations) { operationsConsumed = operations; }

		public synchronized void incrementOperations() { operationsConsumed++; }
		public synchronized void increaseCompletionBy(double amount) { completion += amount; }
	}

	public static Result runSimulation(List<Line2D> edges, Network network, boolean doGraphics) {
		final World world = World.newInstance(edges, doGraphics);
		final Car CAR = world.getCar();

		final Result result = new Result();

		Future<?> simFuture = EXECUTOR.submit(() -> {
			final Result r = world.run();
			result.setOperations(r.getOperations());
			result.setCompletion(r.getCompletion());
			world.cleanUp();
		});
		Future<?> netFuture = null;

		if (network != null) {	//null for manual control
			if (network.getOutputNodes().size() != 5) {    //make this not a constant?
				System.err.println("Bad network: need exactly 5 output nodes");
			}
			else {
				netFuture = EXECUTOR.submit(() -> {

					while (!world.isStopped()) {

						List<Double> results =
								network.compute(
										scaleToRange(CAR.getReadings(), 0, 500, 0, 1)
								);

						CAR.setTurningLeft(results.get(0) > 0.5);
						CAR.setTurningRight(results.get(1) > 0.5);
						CAR.setAccelerating(results.get(2) > 0.5);
						CAR.setDecelerating(results.get(3) > 0.5);
						CAR.setBraking(results.get(4) > 0.5);

						try {
							Thread.sleep(UPDATE_INTERVAL);

							if (world.isPaused())
								world.waitForUnpause();
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}

					}

				});
			}
		}

		awaitCompletion(simFuture, netFuture);

		return result;
	}


	/**
	 * Waits for all futures to complete.
	 * This method blocks until all futures have completed.
	 */
	private static void awaitCompletion(Future<?>... futures) {
		try {
			for (Future<?> f : futures)
				if (f != null) f.get();
		}
		catch (InterruptedException | ExecutionException e) {
			System.err.println("Things went wrong during simulation...");
			e.printStackTrace();
		}
	}


	/**
	 * Scales inputs to a specific range.
	 * Given a list of doubles, map each value from it's original range to the equivalent
	 * value in specified range. 1 in range [0, 6] would be mapped to 1.5 in range [1, 4].
	 */
	private static List<Double> scaleToRange(List<Double> inputs,
											 double originalLower, double originalUpper,
											 double lower, double upper) {
		final List<Double> results = new ArrayList<>();

		final double factor = (upper - lower) / (originalUpper - originalLower);

		for (double input : inputs)
			results.add(lower + factor * input);

		return results;
	}


	/**
	 * Shuts down the simulation.
	 */
	public static void shutdown() {
		EXECUTOR.shutdown();
	}
}
