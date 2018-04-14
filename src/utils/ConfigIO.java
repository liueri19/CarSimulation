package utils;

import ga.Config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ConfigIO {
	public static void write(Config config) throws IOException {
		Date now = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");

		BufferedWriter writer = new BufferedWriter(
				new FileWriter("Config_" + format.format(now) + ".config")
		);

		for (Map.Entry<String, String> entry : config.entrySet()) {
			writer.write(entry.getKey());
			writer.write('=');
			writer.write(entry.getValue());
			writer.newLine();
		}

		writer.close();
	}


	public static void writeSilently(Config config) {
		try {
			write(config);
		}
		catch (IOException e) {
			System.err.println("Failed to write config");
			e.printStackTrace();
		}
	}


	public static Config read(String path) throws IOException {
		final Config config = new Config();

		Files.lines(Paths.get(path))
				.map(line -> line.split("=", 2))
				.forEach(pair -> config.put(pair[0], pair[1]));

		return config;
	}


	public static Config readSilently(String path) {
		final Config config = null;

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
