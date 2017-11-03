package simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Represents a car to be controlled. A car can only be turned
 * by a specific amount and accelerate or brake (as apposed to set
 * to an arbitrary value).
 * <p>
 * A car has 8 sensors, one pointing straight forward, one straight back,
 * two on each side pointing PI/8 away from the straight forward, two each
 * side pointing PI/4 away from forward, last two pointing sideways.
 * <p>
 * The heading of the car is represented in radians, with 0 facing the positive
 * x direction.
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
	public static final double DECELERATION = 0.2;	//may be changed to be different from acceleration
	public static final double TURN_AMOUNT = Math.PI / 180;
	/**
	 * Width and height of the car, used for painting and collision detection.
	 */
	public static final double WIDTH = 40, HEIGHT = 70;
	/**
	 * An angle in radians. This is the actual direction the car is facing.
	 * The 0 should be the positive x axis.
	 */
	private double heading = 0;
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
	private volatile boolean accelerating, decelerating, braking, turningLeft, turningRight;

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
		//using WIDTH then HEIGHT would draw a car with heading 0 facing up,
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
	 * 0 should be the positive x axis.
	 * @return the current heading
	 */
	public synchronized double getHeading() {
		return heading;
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

	protected synchronized void setHeading(double heading) {
		this.heading = heading;
	}

	/**
	 * Accelerate this car by {@link #ACCELERATION}.
	 * @return the velocity after acceleration
	 */
	public synchronized double accelerate() {
		speed += ACCELERATION;
		return speed;
	}

	/**
	 * Decelerate this car by {@link #ACCELERATION}.
	 * @return the velocity after deceleration
	 */
	public synchronized double decelerate() {
		speed -= ACCELERATION;
		return speed;
	}

	/**
	 * Attempt to stop the car, decelerate or accelerate depending
	 * on the current velocity.
	 * Change the velocity toward 0 by {@link #DECELERATION}
	 * @return	the velocity after brake
	 */
	public synchronized double brake() {
		if (speed > DECELERATION)
			speed -= DECELERATION;
		else if (speed < -DECELERATION)
			speed += DECELERATION;
		else
			speed = 0;
		return speed;
	}

	/**
	 * Turn steering by {@link #TURN_AMOUNT} radians to the left.
	 * @return the updated heading
	 */
	public synchronized double turnLeft() {
		heading += TURN_AMOUNT;
		return heading;
	}

	/**
	 * Turn steering by {@link #TURN_AMOUNT} radians to the right.
	 * @return the updated heading
	 */
	public synchronized double turnRight() {
		heading -= TURN_AMOUNT;
		return heading;
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

	public boolean isBraking() {
		return braking;
	}

	public void setBraking(boolean braking) {
		this.braking = braking;
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
	 * Update the location of this car based on the current speed and heading.
	 */
	protected synchronized void update() {
		xC += getSpeed() * Math.cos(getHeading());
		x = xC - HEIGHT / 2;
		yC += getSpeed() * Math.sin(getHeading());
		y = - yC - HEIGHT / 2;

		if (isAccelerating())
			accelerate();
		if (isDecelerating())
			decelerate();
		if (isBraking())
			brake();

		//if no speed, no turning
		if (getSpeed() == 0)
			return;
		if (getSpeed() > 0) {	//normal forward
			if (isTurningLeft())
				turnLeft();
			if (isTurningRight())
				turnRight();
		}
		//reversing requires special turning
//		else if (getSpeed() < 0) {
		else {
			if (isTurningLeft())
				turnRight();
			if (isTurningRight())
				turnLeft();
		}
	}
}
