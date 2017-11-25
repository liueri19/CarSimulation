package simulation;

import network.Network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the ground where cars should be driving on.
 */
public class Track extends JPanel implements KeyListener {
	public static final int WIDTH = 800, HEIGHT = 600;
	/**
	 * Defines the edges of the track.
	 */
	private static final Line2D[] TRACK_EDGES = {
		//TODO write a drawing program to gather data
	};
	private final Car car = new Car(this, 200, 200);

	//stop and pause must not be modified outside main thread
	private volatile boolean stop = false;	//for stopping simulation and network
	private volatile boolean pause = false;	//for pausing game clock
	//shift entire world right and up by the following x and y values
	private int shiftX, shiftY;

	public static void main(String[] args) {
		Track track = new Track();
		JFrame frame = new JFrame("Simulation");
		track.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		track.setBackground(Color.LIGHT_GRAY);
		frame.add(track);
		frame.addKeyListener(track);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

		Network network = new Network(track.car);

		final long updateInterval = 10;
		//one for simulation update, one for network
		//this design may be changed to merge those two threads
		ExecutorService executor = Executors.newSingleThreadExecutor();
		//for simulation
		executor.submit(() -> {
			long last = System.nanoTime();
			while (!track.stop) {
				if (track.pause)
					continue;
				if (System.nanoTime() - last >= 10000000) {	//if 10 milliseconds passed
					track.updateSimulation();
					last = System.nanoTime();
				}
			}
		});
		//for network
		executor.submit(() -> {
			while (!track.stop) {
				network.act();
				try {
					Thread.sleep(updateInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		executor.shutdown();
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

		//DEBUG
		drawGrid(g2D);
//		g2D.fill(new Rectangle2D.Double(car.getXCoordinate()-0.5, -car.getYCoordinate()-0.5, 1, 1));
//		System.out.printf("X: %f\tY: %f%n", car.getXCoordinate(), -car.getYCoordinate());
//		g2D.fill(new Rectangle2D.Double(car.getX()-0.5, car.getY()-0.5, 1, 1));
//		g2D.draw(new Rectangle2D.Double(car.getX(), car.getY(), car.getWidth(), car.getHeight()));
	}

	/**
	 * Draw a grid static to the world as a reference to the coordinate system.
	 */
	private void drawGrid(Graphics2D g) {
		//TODO implement draw grid
	}

	Line2D[] getTrackEdges() {
		return TRACK_EDGES;
	}

	/**
	 * Check if the program has terminated.
	 * @return	true if the program has stopped, false otherwise
	 */
	public boolean isStopped() {
		return stop;
	}

	/**
	 * Check if the program has paused.
	 * @return	true if the program has paused, false otherwise
	 */
	public boolean isPaused() {
		return pause;
	}

	/**
	 * Check if car crashed, update car, update graphics.
	 */
	private void updateSimulation() {
		//could be replaced with sensor checks
		for (Line2D line : TRACK_EDGES) {
			if (car.intersectsLine(line)) {
				//car crashed
				break;
			}
		}
		car.update();

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
		char keyChar = e.getKeyChar();
		if (keyChar == 'r')    //reset car location
			car.setTo(200, -200);
		else if (keyChar == 'p')
			pause = !pause;
	}
}
