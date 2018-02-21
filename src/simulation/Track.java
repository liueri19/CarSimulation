package simulation;

import network.Network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.*;
import java.util.concurrent.*;

/**
 * This class is the ground where cars should be driving on.
 */
public class Track extends JPanel implements KeyListener {
	public static final int WIDTH = 800, HEIGHT = 600;
	private static Track INSTANCE = null;
	/**
	 * Defines the edges of the track.
	 */
	private static final Line2D[] TRACK_EDGES = {
		//TODO convert picture to data?
	};
	private final Car car = new Car(this, 0, 0);

	//stop and pause must not be modified outside main thread
	private volatile boolean stop = false;	//for stopping simulation and network
	private volatile boolean pause = false;	//for pausing game clock


	private Track() {
	}

	public static synchronized Track getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Track();
		return INSTANCE;
	}

	public static void main(String[] args) {
		Track track = getInstance();
		JFrame frame = new JFrame("Simulation");
		track.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		track.setBackground(Color.LIGHT_GRAY);
		frame.add(track);
		frame.addKeyListener(track);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

		Network network = new Network();

		final long updateInterval = 10;
		//one for simulation update, one for network
		//this design may be changed to merge those two threads
		ExecutorService executor = Executors.newFixedThreadPool(2);
		//for simulation
		Future<?> clockFuture = executor.submit(() -> {
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
				network.compute(track.car.getReadings());
				try {
					Thread.sleep(updateInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		try {	//shutdown
			clockFuture.get();
			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		//road edges
		drawEdges(g2D);
		//car
		drawCar(g2D);

		drawGrid(g2D);

		//DEBUG
//		g2D.fill(new Rectangle2D.Double(car.getXCoordinate()-0.5, -car.getYCoordinate()-0.5, 1, 1));
//		System.out.printf("X: %f\tY: %f%n", car.getXCoordinate(), -car.getYCoordinate());
//		g2D.fill(new Rectangle2D.Double(car.getX()-0.5, car.getY()-0.5, 1, 1));
//		g2D.draw(new Rectangle2D.Double(car.getX(), car.getY(), car.getWidth(), car.getHeight()));
	}

	private void drawEdges(Graphics2D g) {
		g.setColor(Color.BLACK);
		for (Line2D edge : TRACK_EDGES)
			g.draw(edge);
	}

	private void drawCar(Graphics2D g) {
		//prepare rotation
		AffineTransform rotation =
				AffineTransform.getRotateInstance(-car.getHeading(), 	//negative due to graphics coordinate plane
						car.getXCoordinate(), -car.getYCoordinate());
		//rotate car
		Shape carTransformed = rotation.createTransformedShape(car);
		//draw car
		g.setColor(Car.DEFAULT_COLOR);
		g.fill(carTransformed);
		g.setColor(Color.BLACK);
		g.draw(carTransformed);	//draw an outline
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
		else if (keyChar == 'q')	//quit
			stop = true;
	}
}
