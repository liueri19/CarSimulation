package simulation;

import network.Network;
import network.NetworkIO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Main {
	public static final long UPDATE_INTERVAL = 10;	//ms

	static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	
	public static void main(String[] args) {
		/*
		2 arguments, both optional:
		map
		network
		 */
		final World WORLD =
				args.length >= 1 ?
						World.newInstance(args[0], true) :
						World.newInstance(Collections.emptyList(), true);
		final Car CAR = WORLD.getCar();


		Future<?> simFuture = EXECUTOR.submit(() -> {
			WORLD.run(); WORLD.cleanUp();
		});
		Future<?> netFuture = null;

		if (args.length >= 2) {
			Network network = NetworkIO.readSilently(args[1]);

			if (network != null) {
				if (network.getOutputNodes().size() != 5) {	//make this not a constant?
					System.err.println("Broken network: need exactly 5 output nodes");
				}
				else {
					netFuture = EXECUTOR.submit(() -> {

						while (!WORLD.isStopped()) {

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

								if (WORLD.isPaused())
									WORLD.waitForUnpause();
							}
							catch (InterruptedException e) {
								e.printStackTrace();
							}

						}

					});
				}
			}
		}

		terminateSilently(EXECUTOR, simFuture, netFuture);

		System.exit(0);
	}


	private static void terminateSilently(ExecutorService executor, Future<?>... futures) {
		try {
			for (Future<?> f : futures)
				if (f != null) f.get();

			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.SECONDS);
		}
		catch (InterruptedException | ExecutionException e) {
			System.err.println("Things went wrong while terminating...");
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
