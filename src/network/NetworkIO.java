package network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class provides utility methods for reading and writing
 * structures of neural networks from and to files.
 */
public class NetworkIO {

	/**
	 * Save the specified network structure to file.
	 */
	public static void write(Network network) throws IOException {
		Date now = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		
		BufferedWriter writer = new BufferedWriter(
				new FileWriter("Network_" + format.format(now) + ".nw")
		);
		
		
	}

	/**
	 * Read the file at the specified path and reconstruct the saved
	 * neural network.
	 * @param path	the path of the saved network
	 * @return	the reconstructed neural network
	 */
	public static Network read(String path) {
		File save = new File(path);
		Network network;
		//read from save
		network = null;
		return network;
	}
}
