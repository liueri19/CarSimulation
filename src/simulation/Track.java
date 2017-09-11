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
	public static final int WIDTH = 800, HEIGHT = 600;
	/**
	 * Defines the edges of the track.
	 */
	private static final Line2D[] TRACK_EDGES = {
		//TODO write a drawing program to gather data
	};
	private Car car = new Car();

	public static void main(String[] args) {
		Track track = new Track();
		JFrame frame = new JFrame("Simulation");
//		frame.setSize(1080, 720);
		track.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		track.setBackground(Color.LIGHT_GRAY);
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
		//draw edges
		g2D.setColor(Color.BLACK);
		for (Line2D edge : TRACK_EDGES)
			g2D.draw(edge);
		//draw car
		g2D.setColor(Car.COLOR);
//		g2D.rotate(car.getDirection(), car.getXCoordinate(), car.getYCoordinate());
		AffineTransform rotation = new AffineTransform();
		rotation.rotate(car.getDirection(), car.getXCoordinate(), car.getYCoordinate());
		Car carTransformed = (Car) rotation.createTransformedShape(car);
		g2D.fill(carTransformed);
		g2D.fill(new Rectangle2D.Double(300, 300, 50, 50));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		System.out.println("Clock update");
		//could be replaced with sensor checks
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
