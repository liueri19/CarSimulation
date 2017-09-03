package simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * This class is the ground where cars should be driving on.
 */
public class Track extends JPanel {
	/**
	 * Defines the edges of the track.
	 */
	private static Line2D[] trackEdges = null;
	private Car car = new Car();

	public static void main(String[] args) {

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

	}
}
