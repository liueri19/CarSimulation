package simulation;

import network.Network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;


/**
 * This class is the ground where cars should be driving on.
 */
public class Track extends JPanel implements KeyListener {
	public static final int WIDTH = 800, HEIGHT = 600;
	
	//initial x and y locations of the center of the car
	private static final int INITIAL_X = WIDTH /2, INITIAL_Y = -HEIGHT /2;
	
	private static Track INSTANCE = null;
	
	/**
	 * Defines the edges of the track.
	 */
	private final List<Line2D> TRACK_EDGES;
	
	private final Car CAR = new Car(this, INITIAL_X, INITIAL_Y);

	//these must not be modified outside main thread
	private volatile boolean stop = false;	//for stopping simulation and network
	private volatile boolean pause = false;	//for pausing game clock
	private volatile boolean verbose = false;	//for verbose output


	private Track(List<Line2D> edges) {
		TRACK_EDGES = edges;
	}

	public static synchronized Track getInstance(List<Line2D> edges) {
		if (INSTANCE == null)
			INSTANCE = new Track(edges);
		return INSTANCE;
	}

	public static void main(String[] args) {
		final List<Line2D> edges;
		if (args.length == 0)
			edges = Collections.emptyList();
		else
			edges = readMapConfig(args[0]);
		Track track = getInstance(edges);


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
				network.compute(track.CAR.getReadings());
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


	//////////////////////////////
	//utility method

	private static List<Line2D> readMapConfig(String fileName) {	//TODO implement read map config
		final List<Line2D> edges = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

		}
		catch (FileNotFoundException e) {
			System.err.printf("File '%s' not found%n", fileName);
		}
		catch (IOException e) {
			System.err.printf("Something went wrong when reading file '%s'...%n", fileName);
			e.printStackTrace();
		}

		return Collections.unmodifiableList(edges);
	}

	
	//////////////////////////////
	//draw stuff
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		
		//road edges
		drawEdges(g2D);
		
		//car
		drawCar(g2D);
		
		if (verbose) {
			//grid
			drawGrid(g2D);
			
			//print coordinate
			g.drawString(
					String.format("X: %.2f, Y: %.2f", CAR.getXCoordinate(), CAR.getYCoordinate()),
					(int) CAR.getCenterX(), (int) CAR.getCenterY()
			);
		}

		//DEBUG
//		g2D.fill(new Rectangle2D.Double(CAR.getXCoordinate()-0.5, -CAR.getYCoordinate()-0.5, 1, 1));
//		System.out.printf("X: %f\tY: %f%n", CAR.getXCoordinate(), -CAR.getYCoordinate());
//		g2D.fill(new Rectangle2D.Double(CAR.getX()-0.5, CAR.getY()-0.5, 1, 1));
//		g2D.draw(new Rectangle2D.Double(CAR.getX(), CAR.getY(), CAR.getWidth(), CAR.getHeight()));
	}

	private void drawEdges(Graphics2D g) {
		g.setColor(Color.BLACK);
		for (Line2D edge : TRACK_EDGES)
			g.draw(edge);
	}

	private void drawCar(Graphics2D g) {
		//prepare rotation
		AffineTransform rotation =
				AffineTransform.getRotateInstance(-CAR.getHeading(), 	//negative due to graphics coordinate plane
						CAR.getCenterX(), CAR.getCenterY());
		//rotate CAR
		Shape carTransformed = rotation.createTransformedShape(CAR);
		//draw CAR
		g.setColor(Car.DEFAULT_COLOR);
		g.fill(carTransformed);
		g.setColor(Color.BLACK);
		g.draw(carTransformed);	//draw an outline
	}

	/**
	 * Draw a grid that is static to the world as a reference to the coordinate system.
	 */
	private void drawGrid(Graphics2D g) {
		Color originalColor = g.getColor();
		g.setColor(Color.BLACK);	//ensure color
		
		final int gridInterval = 150;	//distance between grid lines
		
		final int carX = (int) CAR.getXCoordinate();
		final int carY = (int) CAR.getYCoordinate();
		
		//draw x grid lines
		final int xShift = -(carX % gridInterval);	//lines should go in the opposite direction of the car
		
		for (int x = xShift; x < WIDTH + gridInterval; x += gridInterval) {
			g.drawLine(x, 0, x, HEIGHT);
			g.drawString(
					Integer.toString(carX - WIDTH/2 + x),
					x, HEIGHT);
		}
		
		//draw y grid lines
		final int yShift = carY % gridInterval;	//y coordinates inverted
		
		for (int y = yShift; y < HEIGHT + gridInterval; y += gridInterval) {
			g.drawLine(0, y, WIDTH, y);
			g.drawString(
					Integer.toString(carY + HEIGHT/2 - y),
					0, y);
		}
		
		g.setColor(originalColor);	//reset color
	}
	
	//////////////////////////////
	//some getters

	List<Line2D> getTrackEdges() {
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
	 * Check if CAR crashed, update CAR, update graphics.
	 */
	private void updateSimulation() {
		//could be replaced with sensor checks
		for (Line2D line : TRACK_EDGES) {
			if (CAR.intersectsLine(line)) {
				//car crashed
				break;
			}
		}
		CAR.update();

		repaint();
	}
	
	//////////////////////////////
	//handling key presses

	@Override
	public void keyPressed(KeyEvent e) {
		//turns out letters and arrow keys have different key pressed event behavior
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT)
			CAR.setTurningLeft(true);
		else if (keyCode == KeyEvent.VK_RIGHT)
			CAR.setTurningRight(true);
		else if (keyCode == KeyEvent.VK_UP)
			CAR.setAccelerating(true);
		else if (keyCode == KeyEvent.VK_DOWN)
			CAR.setDecelerating(true);
		else if (keyCode == KeyEvent.VK_SHIFT)
			CAR.setBraking(true);
		System.out.println(KeyEvent.getKeyText(keyCode));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT)
			CAR.setTurningLeft(false);
		else if (keyCode == KeyEvent.VK_RIGHT)
			CAR.setTurningRight(false);
		else if (keyCode == KeyEvent.VK_UP)
			CAR.setAccelerating(false);
		else if (keyCode == KeyEvent.VK_DOWN)
			CAR.setDecelerating(false);
		else if (keyCode == KeyEvent.VK_SHIFT)
			CAR.setBraking(false);
		System.out.println("-" + KeyEvent.getKeyText(keyCode));

		//DEBUG
		if (keyCode == KeyEvent.VK_I)
			System.out.println("##INSPECT");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		char keyChar = e.getKeyChar();
		if (keyChar == 'r')    //reset CAR location
			CAR.setTo(INITIAL_X, INITIAL_Y);
		
		//these flags should be modified only by the main thread
		else if (keyChar == 'p')
			pause = !pause;
		else if (keyChar == 'q')	//quit
			stop = true;
		else if (keyChar == 'v')	//verbose
			verbose = !verbose;
	}
}
