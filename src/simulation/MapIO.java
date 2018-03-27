package simulation;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility methods for reading and writing track edges.
 */
public class MapIO {

	public static void writeMap(List<Line2D> trackEdges) throws IOException {
		Date now = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");

		BufferedWriter writer = new BufferedWriter(
				new FileWriter("Map_" + format.format(now) + ".trackEdges")
		);

		for (Line2D line : trackEdges) {
			writer.write(stringOf(line));
			writer.newLine();
		}

		writer.close();
	}

	public static void writeMapSilently(List<Line2D> trackEdges) {
		try {
			writeMap(trackEdges);
		}
		catch (IOException e) {
			System.err.println("Something went wrong when writing trackEdges");
			e.printStackTrace();
		}
	}


	private static String stringOf(Line2D line) {
		return "(" + line.getX1() + ", " + line.getY1() + ")->(" +
				line.getX2() + ", " + line.getY2() + ")";
	}



	public static List<Line2D> readMap(String fileName) throws IOException {
		final List<Line2D> edges = new ArrayList<>();

		Files.lines(Paths.get(fileName))
				.map(MapIO::parseEdge)
				.forEach(edges::add);

		return edges;
	}

	public static List<Line2D> readMapSilently(String fileName) {
		List<Line2D> trackEdges = null;

		try {
			trackEdges = readMap(fileName);
		}
		catch (IOException e) {
			System.err.printf("Something went wrong when reading file '%s'%n", fileName);
			e.printStackTrace();
		}

		return trackEdges;
	}


	private static Line2D parseEdge(String s) {
		if (!s.matches("\\(\\d, \\d\\)->\\(\\d, \\d\\)"))
			throw new IllegalArgumentException("Incomplete edge: " + s);


		final String[] endPoints = s.split("->");

		final String stringP1, stringP2;
		stringP1 = endPoints[0];
		stringP2 = endPoints[1];

		final Point2D p1, p2;
		p1 = parsePoint(stringP1);
		p2 = parsePoint(stringP2);

		return new Line2D.Double(p1, p2);
	}

	private static Point2D parsePoint(String s) {
		final String[] xy = s.substring(1, s.length()-1).split(", ");
		final double x, y;
		x = Double.parseDouble(xy[0]);
		y = Double.parseDouble(xy[1]);

		return new Point2D.Double(x, y);
	}

}
