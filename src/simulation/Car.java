package simulation;

import javax.swing.*;

/**
 * Represents a car to be controlled. A car can only be turned
 * by a specific amount and accelerate or decelerate (as apposed to set
 * to an arbitrary value).
 * <p>
 * A car has 8 sensors, one pointing straight forward, one straight back,
 * two on each side pointing PI/8 away from the straight forward, two each
 * side pointing PI/4 away from forward, last two pointing sideways.
 */
public class Car {
	/**
	 * In U/ms^2. This is the acceleration constant used when {@link #accelerate()}
	 * is called.
	 */
	public static final int ACCELERATION = 5;
	/**
	 * U/ms^2
	 */
	public static final int DECELERATION = -5;	//may be changed to be different from acceleration
	/**
	 * An angle in radians starting from x-positive going up.
	 */
	private int direction = 0;
	/**
	 * U/ms
	 */
	private int speed = 0;
	private double x = 0, y = 0;

	/**
	 * Get the current speed of the car in units per second.
	 * @return the current speed in units per second
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Returns the x coordinate of this car in relation to
	 * the origin. Not to be confused with {@link JComponent#getX()}.
	 * @return returns the x coordinate in relation to the origin
	 */
	public double getXCoordinate() {
		return x;
	}

	/**
	 * Returns the y coordinate of this car in relation to
	 * the origin. Not to be confused with {@link JComponent#getY()}.
	 * @return returns the y coordinate in relation to the origin
	 */
	public double getYCoordinate() {
		return y;
	}

	/**
	 * Accelerate this car by {@link #ACCELERATION}.
	 * @return the speed after acceleration
	 */
	public int accelerate() {
		speed += ACCELERATION;
		return speed;
	}

	/**
	 * Decelerate this car by {@link #DECELERATION}.
	 * @return the speed after deceleration.
	 */
	public int decelerate() {
		speed += DECELERATION;
		return speed;
	}
}
