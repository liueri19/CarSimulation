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
	 * Arguments:
	 * -Initial population: a directory with network files to start searching from
	 */
	public static void main(String[] args) {
		final List<Network> initialPopulation = new ArrayList<>();

		if (args.length >= 1) {
			Path path = Paths.get(args[0]);

			if (path.toFile().isDirectory()) {
				try (Stream<String> files = getFileNames(path)) {
					files
							.map(NetworkIO::readSilently)
							.filter(Objects::nonNull)
							.forEach(initialPopulation::add);
				}
				catch (IOException e) {
					System.err.println("Failed to read directory: " + args[0]);
				}
			}
			else {
				initialPopulation.add(
						NetworkIO.readSilently(path.toString())
				);
			}
		}


		if (initialPopulation.size() == 0) {
			/*
			reasons this may happen:
				no argument was provided to main;
				no file is present in the specified directory;
				the read failed;
			use default, a network with input nodes connected directly to outputs
			 */
			initialPopulation.add(initCarNetwork());
		}

		System.out.println("Done");
		//TODO implement the actual stuff??
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
				network.addConnection(input, output, 0);
		}

		return network;
	}


	private static Stream<String> getFileNames(Path dir) throws IOException {
		return Files.list(dir)
				.filter(Files::isRegularFile)
				.map(Path::toString);
	}
}
