package simulation;

import network.Network;
import network.NetworkIO;

import java.util.List;
import java.util.concurrent.*;

public class Main {

	static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

	private static final Car CAR = Track.getInstance().getCar();
	
	public static void main(String[] args) {
		/*
		2 arguments, both optional:
		map
		network
		 */

		Future<?> simFuture = EXECUTOR.submit(() -> Track.main(args));
		Future<?> netFuture = null;

		if (args != null && args.length >= 2) {
			Network network = NetworkIO.read(args[1]);

			if (network.getOuts().size() != 5) {
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


		//terminate
		try {
			simFuture.get();
			if (netFuture != null) netFuture.get();

			EXECUTOR.shutdown();
			EXECUTOR.awaitTermination(1, TimeUnit.SECONDS);
		}
		catch (InterruptedException | ExecutionException e) {
			System.err.println("Things went wrong...");
			e.printStackTrace();
		}

		System.exit(0);
	}
}
