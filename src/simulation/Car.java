package simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Represents a car to be controlled. A car can only be turned
 * by a specific amount and accelerate or decelerate (as apposed to set
 * to an arbitrary value).
 * <p>
 * A car has 8 sensors, one pointing straight forward, one straight back,
 * two on each side pointing PI/8 away from the straight forward, two each
 * side pointing PI/4 away from forward, last two pointing sideways.
 */
public class Car extends Rectangle2D.Double {
	/**
	 * Default color for rendering cars.
	 */
	public static final Color COLOR = new Color(50, 255, 50, 100);
	/**
	 * In U/ms^2. This is the acceleration constant used when {@link #accelerate()}
	 * is called.
	 */
	public static final double ACCELERATION = 5;
	/**
	 * U/ms^2
	 */
	public static final double DECELERATION = -5;	//may be changed to be different from acceleration
	public static final double TURN_AMOUNT = Math.PI / 15;
	/**
	 * Width and height of the car, used for painting and collision detection.
	 */
	public static final double WIDTH = 40, HEIGHT = 70;
	/**
	 * An angle in radians.
	 */
	private double direction = - Math.PI / 2;
	/**
	 * U/ms
	 */
	private double speed = 1;
	/**
	 * x and y coordinates of the center of the car
	 */
	private double xC, yC;
	/**
	 * Construct a car at (10, 10) with default width and height.
	 */
	private Sensor sensorL, sensorR, sensorF, sensorB, sensorFL, sensorFR, sensorLF, sensorRF;
	private Sensor[] sensors = {
			sensorL, sensorLF, sensorFL, sensorF, sensorFR, sensorRF, sensorR
	};

	public Car() {
		super(100, 100, WIDTH, HEIGHT);
		xC = getX() + WIDTH / 2D;
		yC = getY() + HEIGHT / 2D;
	}

	/**
	 * Get the current speed of the car in units per second.
	 * @return the current speed in units per second
	 */
	public synchronized double getSpeed() {
		return speed;
	}

	/**
	 * Get the current heading of the car in radians.
	 * @return the current heading
	 */
	public synchronized double getDirection() {
		return direction;
	}

	/**
	 * Returns the x coordinate of the center of this car in relation to
	 * the origin. Not to be confused with {@link JComponent#getX()}.
	 * @return returns the x coordinate in relation to the origin
	 */
	public synchronized double getXCoordinate() {
		return xC;
	}

	/**
	 * Returns the y coordinate of the center of this car in relation to
	 * the origin. Not to be confused with {@link JComponent#getY()}.
	 * @return returns the y coordinate in relation to the origin
	 */
	public synchronized double getYCoordinate() {
		return yC;
	}

	/**
	 * Accelerate this car by {@link #ACCELERATION}.
	 * @return the speed after acceleration
	 */
	public synchronized double accelerate() {
		speed += ACCELERATION;
		return speed;
	}

	/**
	 * Decelerate this car by {@link #DECELERATION}.
	 * @return the speed after deceleration.
	 */
	public synchronized double decelerate() {
		speed += DECELERATION;
		return speed;
	}

	/**
	 * Turn this car by {@link #TURN_AMOUNT} radians to the left.
	 * @return the updated direction
	 */
	public synchronized double turnLeft() {
		direction += TURN_AMOUNT;
		return direction;
	}

	/**
	 * Turn this car by {@link #TURN_AMOUNT} radians to the right.
	 * @return the updated direction
	 */
	public synchronized double turnRight() {
		direction -= TURN_AMOUNT;
		return  direction;
	}

	/**
	 * Update the location of this car based on the current speed and direction.
	 */
	protected synchronized void update() {
		xC += getSpeed() * Math.cos(getDirection());
		x = xC - WIDTH / 2;
		yC += getSpeed() * Math.sin(getDirection());
		y = y - HEIGHT / 2;
//		turnRight();
		//print state
//		System.out.printf("Car: %f, %f%n", xC, yC);
	}
}
