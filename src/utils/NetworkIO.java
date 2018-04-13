package utils;

import network.Connection;
import network.Network;
import network.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class provides utility methods for reading and writing
 * structures of neural networks from and to files.
 */
public class NetworkIO {

	/**
	 * Saves the specified network structure to file.
	 */
	public static void write(Network network) throws IOException {
		Date now = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		
		BufferedWriter writer = new BufferedWriter(
				new FileWriter("Network_" + format.format(now) + ".nw")
		);

		//write input nodes
		for (Node i : network.getInputNodes()) {
			writer.write(i.toString());
			writer.write(", ");
		}
		writer.newLine();

		//write hidden nodes
		for (Node h : network.getHiddens().values()) {
			writer.write(h.toString());
			writer.write(", ");
		}
		writer.newLine();

		//write output nodes
		for (Node o : network.getOutputNodes()) {
			writer.write(o.toString());
			writer.write(", ");
		}
		writer.newLine();

		//write connections
		for (Connection c : network.getConnections()) {
			writer.write(c.toString());
			writer.newLine();
		}

		writer.close();
	}

	public static void writeSilently(Network network) {
		try {
			write(network);
		}
		catch (IOException e) {
			System.err.println("Failed to write network");
			e.printStackTrace();
		}
	}

	/**
	 * Reads the file at the specified path and reconstructs the saved
	 * neural network.
	 * @param path	the path of the saved network
	 * @return	the reconstructed neural network
	 */
	public static Network read(String path) throws IOException {
		Path save = Paths.get(path);

		Network network = new Network();

		final String[] lines = Files.lines(save).toArray(String[]::new);

		// for first 3 lines
		for (int i = 0; i < 3; i++) {
			final String line = lines[i];
			if (line.isEmpty()) continue;
			parseNodeLine(line).forEach(network::putNode);
		}

		// rest of the file
		for (int i = 3; i < lines.length; i++) {
			network.putConnection(
					Connection.parseConnection(lines[i])
			);
		}

		return network;
	}

	private static List<Node> parseNodeLine(String line) {
		String[] nodeStrs = line.split(", ");
		List<Node> nodes = new ArrayList<>();

		for (String n : nodeStrs)
			nodes.add(Node.parseNode(n));

		return nodes;
	}


	/**
	 * Silently reads the file at the specified path and reconstructs the saved
	 * neural network.
	 * Instead of throwing IOException, this methods returns null for failed operations.
	 */
	public static Network readSilently(String file) {
		Network network = null;

		try {
			network = read(file);
		}
		catch (IOException e) {
			System.err.println("Failed to read file: " + file);
			e.printStackTrace();
		}

		return network;
	}
}
