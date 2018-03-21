package simulation;

import network.Network;
import network.NetworkIO;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class Main {

	static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	private static final Car CAR = new Track().getCar();
	
	public static void main(String[] args) {
		/*
		2 arguments, both optional:
		map
		network
		 */

		Future<?> simFuture = EXECUTOR.submit(() -> Track.main(args));
		Future<?> netFuture = null;

		if (args != null && args.length >= 2) {
			Network network = silentlyRead(args[1]);

			if (network != null) {
				if (network.getOuts().size() != 5) {	//make this not a constant?
					System.err.println("Broken network: need exactly 5 output nodes");
				}
				else {
					netFuture = EXECUTOR.submit(() -> {
						List<Double> results = network.compute(CAR.getReadings());

						int n = 0;
						CAR.setTurningLeft(results.get(n++) > 0.5);
						CAR.setTurningRight(results.get(n++) > 0.5);
						CAR.setAccelerating(results.get(n++) > 0.5);
						CAR.setDecelerating(results.get(n++) > 0.5);
						CAR.setBraking(results.get(n) > 0.5);

						try {
							Thread.sleep(10);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					});
				}
			}
		}


		silentlyTerminate(EXECUTOR, simFuture, netFuture);

		System.exit(0);
	}


	private static Network silentlyRead(String file) {
		Network network = null;

		try {
			network = NetworkIO.read(file);
		}
		catch (IOException e) {
			System.err.println("Failed to read file: " + file);
			e.printStackTrace();
		}

		return network;
	}


	private static void silentlyTerminate(ExecutorService executor, Future<?>... futures) {
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
}
