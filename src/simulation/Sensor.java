package simulation;

/**
 * A sensor that is capable of measuring a distance. It measures the
 * line segment formed by one point extending toward a specific direction
 * until blocked by a surface.
 */
public class Sensor {
	/**
	 * A angle in radians describing the facing of the sensor.
	 * This value assumes a 0 along the a axis.
	 */
	private final double direction;
	private final Car host;	//only for x y information

	/**
	 * Construct a sensor facing the specified direction on the specified
	 * car. The direction is an angle in radians in relation to the x axis.
	 * @param car		the car to install the sensor on
	 * @param direction	the facing of the sensor
	 */
	public Sensor(Car car, double direction) {
		this.direction = direction;
		host = car;
	}

	/**
	 * Measure the distance of a line segment formed by one point extending
	 * toward a specific direction until blocked by a surface or reaching the
	 * maximum of 500 units.
	 * @return the distance measured
	 */
	public double measureDistance() {
		/*
		 */
		return 0d;	//placeholder
	}
}
