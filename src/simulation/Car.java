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
	public static final Color DEFAULT_COLOR = new Color(50, 255, 50, 100);
	/**
	 * In U/ms^2. This is the acceleration constant used when {@link #accelerate()}
	 * is called.
	 */
	public static final double ACCELERATION = 0.1;
	/**
	 * U/ms^2
	 */
	public static final double DECELERATION = -0.2;	//may be changed to be different from acceleration
	public static final double TURN_AMOUNT = Math.PI / 180;
	/**
	 * Width and height of the car, used for painting and collision detection.
	 */
	public static final double WIDTH = 40, HEIGHT = 70;
	/**
	 * An angle in radians.
	 * The 0 should be the positive x axis direction.
	 */
	private double direction = 0;
	/**
	 * U/ms
	 */
	private double speed = 0;
	/**
	 * x and y coordinates of the center of the car
	 */
	private double xC, yC;

	/**
	 * For example, if accelerating is true, the car should accelerate every update
	 */
	private volatile boolean accelerating, decelerating, turningLeft, turningRight;

	private Sensor sensorL, sensorR, sensorF, sensorB, sensorFL, sensorFR, sensorLF, sensorRF;
	private Sensor[] sensors = {
			sensorL, sensorLF, sensorFL, sensorF, sensorFR, sensorRF, sensorR
	};

//	/**
//	 * Construct a car at (100, 100) with default width and height.
//	 */
//	public Car() {
//		this(100, 100);
//	}

	/**
	 * Construct a car at the specified locations with default width and height.
	 * @param x	the upper left x coordinate
	 * @param y	the upper left y coordinate
	 */
	public Car(int x, int y) {
		//using WIDTH then HEIGHT would draw a car with direction 0 facing up,
		//as they are taken as the width and height of the rectangle
		super(x, y, HEIGHT, WIDTH);
		xC = getX() + HEIGHT / 2;
		yC = -getY() - WIDTH / 2;
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
	 * Set the center coordinate of this car to the specified coordinate.
	 * @param x	the x coordinate to set the center of this car to
	 * @param y	the y coordinate to set the center of this car to
	 */
	protected synchronized void setTo(int x, int y) {
		xC = x;
		yC = y;
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

	public boolean isAccelerating() {
		return accelerating;
	}

	public void setAccelerating(boolean accelerating) {
		this.accelerating = accelerating;
	}

	public boolean isDecelerating() {
		return decelerating;
	}

	public void setDecelerating(boolean decelerating) {
		this.decelerating = decelerating;
	}

	public boolean isTurningLeft() {
		return turningLeft;
	}

	public void setTurningLeft(boolean turningLeft) {
		this.turningLeft = turningLeft;
	}

	public boolean isTurningRight() {
		return turningRight;
	}

	public void setTurningRight(boolean turningRight) {
		this.turningRight = turningRight;
	}


	/**
	 * Update the location of this car based on the current speed and direction.
	 */
	protected synchronized void update() {
		xC += getSpeed() * Math.cos(getDirection());
		x = xC - HEIGHT / 2;
		yC += getSpeed() * Math.sin(getDirection());
		y = - yC - HEIGHT / 2;

		if (accelerating)
			accelerate();
		if (decelerating)
			decelerate();
		if (turningLeft)
			turnLeft();
		if (turningRight)
			turnRight();
	}
}
