package simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;

/**
 * This class is the ground where cars should be driving on.
 */
public class Track extends JPanel implements ActionListener {
	/**
	 * Defines the edges of the track.
	 */
	private static final Line2D[] TRACK_EDGES = {};
	private Car car = new Car();

	public static void main(String[] args) {
		Track track = new Track();
		JFrame frame = new JFrame("Simulation");
//		frame.setSize(1080, 720);
		track.setPreferredSize(new Dimension(1920, 1080));
		track.setBackground(Color.WHITE);
		frame.add(track);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
		Timer clock = new Timer(10, track);
		clock.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		//TODO paint the stuff
		g2D.setColor(Color.BLACK);
		for (Line2D edge : TRACK_EDGES)
			g2D.draw(edge);
		g2D.setColor(Car.COLOR);
//		g2D.rotate(car.getDirection(), car.getXCoordinate(), car.getYCoordinate());
		g2D.draw(car);
		g2D.fill(car);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (Line2D line : TRACK_EDGES) {
			if (car.intersectsLine(line)) {
				//car crashed
				break;
			}
		}
		car.update();
//		System.out.printf("xC: %f, yC: %f, D: %f, V: %f%n",
//				car.getXCoordinate(), car.getYCoordinate(), car.getDirection(), car.getSpeed());
		repaint();
	}
}
