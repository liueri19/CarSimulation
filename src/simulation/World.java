package simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.List;

/**
 * This class is the ground where cars should be driving on.
 */
public class World extends JPanel implements KeyListener {
	private static final int WIDTH = 800, HEIGHT = 600;

	//initial x and y locations of the center of the car
	private static final int INITIAL_X = WIDTH / 2, INITIAL_Y = -HEIGHT / 2;

//	/**
//	 * Defines the edges of the track.
//	 */
//	private final List<Line2D> TRACK_EDGES;
	private final Map MAP;

	private final Car CAR = new Car(this, INITIAL_X, INITIAL_Y);

	private final JFrame holdingFrame;

	//these may only be modified in response to key events
	private volatile boolean stop = false;    //for stopping simulation and network
	private volatile boolean verbose = false;    //for verbose output
	private volatile boolean pause = false;    //for pausing game clock

	//monitor for clock to wait on during pause
	private final Object PAUSE_MONITOR = new Object();

	void waitForUnpause() throws InterruptedException {
		synchronized (PAUSE_MONITOR) {
			while (isPaused())
				PAUSE_MONITOR.wait();
		}
	}


	private World(JFrame frame, Map map) {
		holdingFrame = frame;
		MAP = map;
	}

	public static void main(String[] args) {
		final Map map =
				args.length >= 1 ? Map.readMap(args[0]) : new Map(Collections.emptyList());

		World world = World.newInstance(map, true);

		world.run();

		world.cleanUp();
	}

	static World newInstance(Map map, boolean doGraphics) {
		JFrame frame = new JFrame("( ͡° ͜ʖ ͡°)");
		World world = new World(frame, map);

		if (doGraphics) {
			world.setPreferredSize(new Dimension(WIDTH, HEIGHT));
			world.setBackground(Color.LIGHT_GRAY);
			frame.add(world);
			frame.addKeyListener(world);
			frame.pack();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
		}

		return world;
	}

	static World newInstance(String mapFile, boolean doGraphics) {
		return newInstance(Map.readMap(mapFile), doGraphics);
	}

	void run() {
		while (!stop) {
			try {
				Thread.sleep(Main.UPDATE_INTERVAL);

				if (isPaused())
					waitForUnpause();

				updateSimulation();
			}
			catch (InterruptedException e) {
				System.err.println("Simulation interrupted");
				e.printStackTrace();
				break;
			}
		}
	}

	void cleanUp() {
		holdingFrame.removeKeyListener(this);
		holdingFrame.dispose();
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
		//TODO shift edges in respect to car
	}

	private void drawCar(Graphics2D g) {
		//prepare rotation
		AffineTransform rotation =
				AffineTransform.getRotateInstance(-CAR.getHeading(),    //negative due to graphics coordinate plane
						CAR.getCenterX(), CAR.getCenterY());
		//rotate CAR
		Shape carTransformed = rotation.createTransformedShape(CAR);
		//draw CAR
		g.setColor(Car.DEFAULT_COLOR);
		g.fill(carTransformed);
		g.setColor(Color.BLACK);
		g.draw(carTransformed);    //draw an outline
	}

	/**
	 * Draws a grid that is static to the world as a reference to the coordinate system.
	 */
	private void drawGrid(Graphics2D g) {
		Color originalColor = g.getColor();
		g.setColor(Color.BLACK);    //ensure color

		final int gridInterval = 150;    //distance between grid lines

		final int carX = (int) CAR.getXCoordinate();
		final int carY = (int) CAR.getYCoordinate();

		//draw x grid lines
		final int xShift = -(carX % gridInterval);    //lines should go in the opposite direction of the car

		for (int x = xShift; x < WIDTH + gridInterval; x += gridInterval) {
			g.drawLine(x, 0, x, HEIGHT);
			g.drawString(
					Integer.toString(carX - WIDTH / 2 + x),
					x, HEIGHT);
		}

		//draw y grid lines
		final int yShift = carY % gridInterval;    //y coordinates inverted

		for (int y = yShift; y < HEIGHT + gridInterval; y += gridInterval) {
			g.drawLine(0, y, WIDTH, y);
			g.drawString(
					Integer.toString(carY + HEIGHT / 2 - y),
					0, y);
		}

		g.setColor(originalColor);    //reset color
	}

	//////////////////////////////
	//some getters
	Map getMap() { return MAP; }

	List<Line2D> getTrackEdges() { return getMap().getEdges(); }

	Car getCar() { return CAR; }

	/**
	 * Checks if the program has terminated.
	 */
	public boolean isStopped() { return stop; }

	/**
	 * Checks if the program has paused.
	 */
	public boolean isPaused() { return pause; }

	/**
	 * Checks if car crashed, update car, update graphics.
	 */
	private void updateSimulation() {
		//could be replaced with sensor checks
		for (Line2D line : getTrackEdges()) {
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
		//letters and arrow keys have different key pressed event behavior
		handleKeyEvent(e, true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		handleKeyEvent(e, false);

		//DEBUG
		if (e.getKeyCode() == KeyEvent.VK_I)
			System.out.println("##INSPECT");
	}

	private void handleKeyEvent(KeyEvent e, boolean isKeyPress) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT)
			CAR.setTurningLeft(isKeyPress);
		else if (keyCode == KeyEvent.VK_RIGHT)
			CAR.setTurningRight(isKeyPress);
		else if (keyCode == KeyEvent.VK_UP)
			CAR.setAccelerating(isKeyPress);
		else if (keyCode == KeyEvent.VK_DOWN)
			CAR.setDecelerating(isKeyPress);
		else if (keyCode == KeyEvent.VK_SHIFT)
			CAR.setBraking(isKeyPress);

		if (!isKeyPress)
			System.out.print('-');
		System.out.println(KeyEvent.getKeyText(keyCode));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		char keyChar = e.getKeyChar();
		if (keyChar == 'r')    //reset CAR location
			CAR.setTo(INITIAL_X, INITIAL_Y);

			//these flags should be modified only by key events
		else if (keyChar == 'p') {    //pause
			pause = !pause;

			if (!isPaused()) {	//un-pause
				synchronized (PAUSE_MONITOR) {
					PAUSE_MONITOR.notifyAll();
				}
			}
		}
		else if (keyChar == 'q')    //quit
			stop = true;
		else if (keyChar == 'v')    //verbose
			verbose = !verbose;
	}
}
