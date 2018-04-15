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

	static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	
	public static void main(String[] args) throws Exception {
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

		EXECUTOR.shutdown();
		EXECUTOR.awaitTermination(1, TimeUnit.SECONDS);

		System.exit(0);
	}


	/**
	 * A class that bundles information about the result of a simulation run.
	 */
	public static class Result {
		private long operationsConsumed;
		private double completion;

		private Result() {}
		private Result(long operations, double completion) {
			operationsConsumed = operations;
			this.completion = completion;
		}

		public double getCompletion() { return completion; }
		public long getOperations() { return operationsConsumed; }
		private void setOperations(long operations) { operationsConsumed = operations; }
		private void setCompletion(double completion) { this.completion = completion; }
	}

	public static Result runSimulation(List<Line2D> edges, Network network, boolean doGraphics) {
		final World world = World.newInstance(edges, doGraphics);
		final Car CAR = world.getCar();

		final Result result = new Result();


		Future<?> simFuture = EXECUTOR.submit(() -> {
			// TODO set result operations count
			// TODO set result completion
			world.run(); world.cleanUp();
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

						int n = 0;
						CAR.setTurningLeft(results.get(n++) > 0.5);
						CAR.setTurningRight(results.get(n++) > 0.5);
						CAR.setAccelerating(results.get(n++) > 0.5);
						CAR.setDecelerating(results.get(n++) > 0.5);
						CAR.setBraking(results.get(n) > 0.5);

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

		awaitCompletion(EXECUTOR, simFuture, netFuture);

		return result;
	}


	private static void awaitCompletion(ExecutorService executor, Future<?>... futures) {
		try {
			for (Future<?> f : futures)
				if (f != null) f.get();

//			executor.shutdown();
//			executor.awaitTermination(1, TimeUnit.SECONDS);
		}
		catch (InterruptedException | ExecutionException e) {
			System.err.println("Things went wrong during simulation...");
			e.printStackTrace();
		}
	}


	private static List<Double> scaleToRange(List<Double> inputs,
											 double originalLower, double originalUpper,
											 double lower, double upper) {
		final List<Double> outputs = new ArrayList<>();

		final double factor = (upper - lower) / (originalUpper - originalLower);

		for (double input : inputs)
			outputs.add(factor * input);

		return outputs;
	}
}
