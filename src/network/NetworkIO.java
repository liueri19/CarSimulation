package network;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

		Files.lines(save)
				.map(Connection::parseConnection)
//				.filter(Objects::nonNull)
				.forEach(network::putConnection);

		return network;
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
