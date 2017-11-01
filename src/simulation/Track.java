package simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.*;

/**
 * This class is the ground where cars should be driving on.
 */
public class Track extends JPanel implements ActionListener, KeyListener {
	public static final int WIDTH = 800, HEIGHT = 600;
	/**
	 * Defines the edges of the track.
	 */
	private static final Line2D[] TRACK_EDGES = {
		//TODO write a drawing program to gather data
	};
	private final Car car = new Car(200, 200);

	public static void main(String[] args) {
		Track track = new Track();
		JFrame frame = new JFrame("Simulation");
//		frame.setSize(1080, 720);
		track.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		track.setBackground(Color.LIGHT_GRAY);
		frame.add(track);
		frame.addKeyListener(track);
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
		//draw road edges
		g2D.setColor(Color.BLACK);
		for (Line2D edge : TRACK_EDGES)
			g2D.draw(edge);
		//prepare rotation
		//TODO fix bug: rotation seems to be centered around weird point
		AffineTransform rotation =
				AffineTransform.getRotateInstance(-car.getHeading(), 	//negative due to graphics coordinate plane
						car.getXCoordinate(), -car.getYCoordinate());
		//rotate car
		Shape carTransformed = rotation.createTransformedShape(car);
		//draw car
		g2D.setColor(Car.DEFAULT_COLOR);
		g2D.fill(carTransformed);
		g2D.setColor(Color.BLACK);
		g2D.draw(carTransformed);	//draw an outline
//		System.out.printf("xC: %f\tyC: %f\tx: %f\ty: %f\tD: %f\tV: %f%n",
//				car.getXCoordinate(), car.getYCoordinate(), car.getX(), car.getY(), car.getHeading(), car.getSpeed());
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
//		System.out.printf("xC: %f\tyC: %f\tx: %f\ty: %f\tD: %f\tV: %f%n",
//				car.getXCoordinate(), car.getYCoordinate(), car.getX(), car.getY(), car.getHeading(), car.getSpeed());
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//turns out letters and arrow keys have different key pressed event behavior
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT)
			car.setTurningLeft(true);
		else if (keyCode == KeyEvent.VK_RIGHT)
			car.setTurningRight(true);
		else if (keyCode == KeyEvent.VK_UP)
			car.setAccelerating(true);
		else if (keyCode == KeyEvent.VK_DOWN)
			car.setDecelerating(true);
		else if (keyCode == KeyEvent.VK_SHIFT)
			car.setBraking(true);
		System.out.println(KeyEvent.getKeyText(keyCode));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT)
			car.setTurningLeft(false);
		else if (keyCode == KeyEvent.VK_RIGHT)
			car.setTurningRight(false);
		else if (keyCode == KeyEvent.VK_UP)
			car.setAccelerating(false);
		else if (keyCode == KeyEvent.VK_DOWN)
			car.setDecelerating(false);
		else if (keyCode == KeyEvent.VK_SHIFT)
			car.setBraking(false);
		System.out.println("-" + KeyEvent.getKeyText(keyCode));

		//DEBUG
		if (keyCode == KeyEvent.VK_I)
			System.out.println("##INSPECT");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 'r')    //reset car location
			car.setTo(200, -200);
	}
}
