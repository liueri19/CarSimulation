package simulation;

import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Map {
	private final List<Line2D> edges;

	public Map(List<Line2D> edges) {
		this.edges = Collections.unmodifiableList(new ArrayList<>(edges));
	}

	public List<Line2D> getEdges() { return edges; }

	//////////////////////////////
	// read and write map file

	public static void writeMap(Map map) {
		//TODO write map to file
	}

	public static Map readMap(String fileName) {
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			//TODO read map from file
		}
		catch (FileNotFoundException e) {
			System.err.printf("File '%s' not found%n", fileName);
		}
		catch (IOException e) {
			System.err.printf("Something went wrong when reading file '%s'...%n", fileName);
			e.printStackTrace();
		}

		return null;
	}
}
