package ga;

import network.Network;
import network.Node;
import network.NodeType;
import utils.ConfigIO;
import utils.NetworkIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class NeatMain {
	private static final String DEFAULT_CONFIG = "configs/default.config";

	/**
	 * Arguments:
	 * -Initial population: a directory with network files to start searching from
	 * -config file
	 */
	public static void main(String[] args) {
		final List<Network> population = new ArrayList<>();

		if (args.length >= 1) {
			Path path = Paths.get(args[0]);

			if (path.toFile().isDirectory()) {
				try (Stream<String> files = getFileNames(path)) {
					files
							.map(NetworkIO::readSilently)
							.filter(Objects::nonNull)
							.forEach(population::add);
				}
				catch (IOException e) {
					System.err.println("Failed to read directory: " + args[0]);
				}
			}
			else {
				Network n = NetworkIO.readSilently(path.toString());
				if (n != null) population.add(n);
			}
		}

		final Config config = args.length >= 2 ?
				ConfigIO.readSilently(args[1]) :
				ConfigIO.readSilently(DEFAULT_CONFIG);

		System.out.println("Initializing population");

		//init population
		if (population.size() == 0) {
			/*
			reasons this may happen:
				no argument was provided to main;
				no file is present in the specified directory;
				the read failed;
			 */
			initPopulation(population, Integer.parseInt(config.get("population_size")));
		}

		System.out.println("Search started: " + LocalDateTime.now());

		final double targetFitness = Double.parseDouble(config.get("target_fitness"));
		for (double champFitness = 0; champFitness < targetFitness; ) {
			//evaluate fitness
			// TODO evaluate, eliminate, reproduce, mutate

			//eliminate


			//reproduce


			//mutate

		}


		System.out.println("Solution found: " + LocalDateTime.now());

//		population.forEach(NetworkIO::writeSilently);
	}


	private static void initPopulation(List<Network> population, int size) {
		while (population.size() < size)
			//8 inputs for 8 sensors, 5 outputs for 5 controls
			population.add(initEmptyNetwork(8, 5));
	}


	private static Network initEmptyNetwork(int inputsNodes, int outputNodes) {
		final Network network = new Network();
		final List<Node> inputs = network.getInputNodes();
		final List<Node> outputs = network.getOutputNodes();

		for (int i = 0; i < inputsNodes; i++)
			inputs.add(new Node.NodeBuilder(NodeType.INPUT).build());

		for (int i = 0; i < outputNodes; i++)
			outputs.add(new Node.NodeBuilder(NodeType.OUTPUT).build());

//		//connect inputs to outputs
//		for (Node input : inputs) {
//			for (Node output : outputs)
//				network.addConnection(input, output, 0, 0);
//		}

		return network;
	}


	private static Stream<String> getFileNames(Path dir) throws IOException {
		return Files.list(dir)
				.filter(Files::isRegularFile)
				.map(Path::toString);
	}
}
