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
				AffineTransform.getRotateInstance(-car.getDirection(), 	//negative due to graphics coordinate plane
						car.getXCoordinate(), -car.getYCoordinate());
		//rotate car
		Shape carTransformed = rotation.createTransformedShape(car);
		//draw car
		g2D.setColor(Car.DEFAULT_COLOR);
		g2D.fill(carTransformed);
		g2D.setColor(Color.BLACK);
		g2D.draw(carTransformed);	//draw an outline
//		System.out.printf("xC: %f\tyC: %f\tx: %f\ty: %f\tD: %f\tV: %f%n",
//				car.getXCoordinate(), car.getYCoordinate(), car.getX(), car.getY(), car.getDirection(), car.getSpeed());
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
//				car.getXCoordinate(), car.getYCoordinate(), car.getX(), car.getY(), car.getDirection(), car.getSpeed());
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		/*
		TODO fix bug
		key register with multiple keys pressed malfunction
		missing registration of key press events
		 */
		int keyCode = e.getKeyCode();
		switch (keyCode) {
			case KeyEvent.VK_A:
				car.setTurningLeft(true);
				break;
			case KeyEvent.VK_D:
				car.setTurningRight(true);
				break;
			case KeyEvent.VK_W:
				car.setAccelerating(true);
				break;
			case KeyEvent.VK_S:
				car.setDecelerating(true);
				break;
			case KeyEvent.VK_SPACE:
				car.setBraking(true);
				break;
		}
		System.out.println(KeyEvent.getKeyText(keyCode));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
			case KeyEvent.VK_A:
				car.setTurningLeft(false);
				break;
			case KeyEvent.VK_D:
				car.setTurningRight(false);
				break;
			case KeyEvent.VK_W:
				car.setAccelerating(false);
				break;
			case KeyEvent.VK_S:
				car.setDecelerating(false);
				break;
			case KeyEvent.VK_SPACE:
				car.setBraking(false);
				break;
		}
		System.out.println("-" + KeyEvent.getKeyText(keyCode));

		//DEBUG
		if (keyCode == KeyEvent.VK_I)
			System.out.println("##INSPECT");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == 'r')    //reset car location
			car.setTo(200, -200);

		System.out.println(KeyEvent.getKeyText(keyCode));
	}
}
