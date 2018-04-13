package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConfigIO {
	public static void write(Map<?, ?> config) throws IOException {
		Date now = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");

		BufferedWriter writer = new BufferedWriter(
				new FileWriter("Config_" + format.format(now) + ".config")
		);

		for (Map.Entry<?, ?> entry : config.entrySet()) {
			writer.write(entry.getKey().toString());
			writer.write('=');
			writer.write(entry.getValue().toString());
			writer.newLine();
		}

		writer.close();
	}


	public static void writeSilently(Map<?, ?> config) {
		try {
			write(config);
		}
		catch (IOException e) {
			System.err.println("Failed to write config");
			e.printStackTrace();
		}
	}


	public static Map<String, String> read(String path) throws IOException {
		final Map<String, String> config = new HashMap<>();

		Files.lines(Paths.get(path))
				.map(line -> line.split("=", 2))
				.forEach(pair -> config.put(pair[0], pair[1]));

		return Collections.unmodifiableMap(config);
	}


	public static Map<String, String> readSilently(String path) {
		final Map<String, String> config = null;

		try {
			read(path);
		}
		catch (IOException e) {
			System.err.println("Failed to read file: " + path);
			e.printStackTrace();
		}

		return config;
	}
}
