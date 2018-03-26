package network;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class NeatMain {
	/**
	 * Networks performing below this percentage of the population will be eliminated.
	 */
	private static final double HARSHNESS = 0.50;
	/**
	 * Number of networks allowed in a generation.
	 */
	private static final int POPULATION_SIZE = 150;


	/**
	 * Arguments:
	 * -Initial population: a directory with network files to start searching from
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
				population.add(
						NetworkIO.readSilently(path.toString())
				);
			}
		}


		if (population.size() == 0) {
			/*
			reasons this may happen:
				no argument was provided to main;
				no file is present in the specified directory;
				the read failed;
			 */
			population.add(initCarNetwork());
		}


//		population.forEach(NetworkIO::writeSilently);
	}


	private static Network initCarNetwork() {
		final Network network = new Network();
		final List<Node> inputs = network.getInputNodes();
		final List<Node> outputs = network.getOutputNodes();

		//8 inputs for 8 sensors
		for (int i = 0; i < 8; i++)
			inputs.add(new Node.NodeBuilder(NodeType.INPUT).build());

		//5 outputs for 5 controls
		for (int i = 0; i < 5; i++)
			outputs.add(new Node.NodeBuilder(NodeType.OUTPUT).build());

		//connect inputs to outputs
		for (Node input : inputs) {
			for (Node output : outputs)
				network.addConnection(input, output, 0, 0);
		}

		return network;
	}


	private static Stream<String> getFileNames(Path dir) throws IOException {
		return Files.list(dir)
				.filter(Files::isRegularFile)
				.map(Path::toString);
	}
}
