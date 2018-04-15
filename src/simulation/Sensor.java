package simulation;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A sensor that is capable of measuring a distance. It measures the
 * line segment formed by one point extending toward a specific direction
 * until blocked by a surface.
 */
public class Sensor {
	/**
	 * A angle in radians describing the facing of the sensor.
	 * This value assumes a 0 along the x axis.
	 * This value is relative to the car.
	 */
	private final double direction;
	private final Car car;
	private final World world;
	private Line2D ray;	//a line to check for intersections
	//the range of the sensor
	private static final double RANGE = 500;

	//update this sensor
	private static final ExecutorService POOL = Executors.newCachedThreadPool();
	private volatile double distance;

	/**
	 * Construct a sensor facing the specified direction on the specified
	 * car. The direction is an angle in radians in relation to the x axis.
	 * @param car		the car to install the sensor on
	 * @param direction	the facing of the sensor
	 */
	public Sensor(Car car, double direction) {
		this.direction = direction;
		this.car = car;
		this.world = car.getWorld();
		ray = new Line2D.Double();

		POOL.submit(() -> {
			while (!world.isStopped()) {
				try {
					//sensor sleeps less
					Thread.sleep(Simulation.UPDATE_INTERVAL /2);

					if (world.isPaused())
						world.waitForUnpause();

					updateRay();
					distance = calculateDistance();
				}
				catch (InterruptedException e) {
					System.err.println("Sensor interrupted");
					e.printStackTrace();
					break;
				}
			}
		});
	}

	/**
	 * Get the direction this sensor is facing.
	 * The direction is relative to the car, not the world.
	 * @return	the direction of this sensor
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * Update the ray such that the starting point is the center of the car
	 * and the ending point is a point extended RANGE units in the specified
	 * direction.
	 */
	private void updateRay() {
		double startX, startY, endX, endY;
		startX = car.getXCoordinate();
		startY = car.getYCoordinate();
		endX = startX + RANGE * Math.cos(car.getHeading() + getDirection());
		endY = startY + RANGE * Math.sin(car.getHeading() + getDirection());
		ray.setLine(startX, startY, endX, endY);
	}

	private double calculateDistance() {
		//there probably are better algorithm than this
		//find distance to all bounds, return smallest
		Point here = new Point(car.getXCoordinate(), car.getYCoordinate());
		Point intersect;
		double distance = RANGE;
		for (Line2D bound : car.getWorld().getTrackEdges()) {
			intersect = getIntersection(bound);
			if (intersect == null)
				continue;
			double temp = here.distance(intersect.getX(), intersect.getY());
			distance = temp < distance ? temp : distance;
		}
		return distance;
	}

	/**
	 * Measure the distance of a line segment formed by one point extending
	 * toward a specific direction until blocked by a surface or reaching the
	 * maximum of RANGE units.
	 * In this case the line segment start at the position of this sensor and
	 * extends in the direction this sensor is facing.
	 * @return the distance measured
	 */
	public double measure() {
		return distance;
	}

	public Point getIntersection(Line2D line) {
		if (!ray.intersectsLine(line))	//if lines don't intersect at all
			return null;

		/*
		theoretical intersection
		x = (c2-c1)/(m1-m2)
		f(x)=m1*x+c1	this
		g(x)=m2*x+c2	line
		find intersection
		 */
		double x, y, m1, m2, c1, c2;
		m1 = (ray.getY2() - ray.getY1()) / (ray.getX2() - ray.getX1());
		m2 = (line.getY2() - line.getY1()) / (line.getX2() - line.getX1());
		c1 = -m1 * ray.getX1() + ray.getY1();
		c2 = -m2 * line.getX1() + line.getY1();
		x = (c2-c1)/(m1-m2);
		y = m1*x + c1;

		/*
		check if on both lines
		a - one end of bound
		b - the other end of bound
		in bound means: a < x < b
		a < x  ->  a-x < 0
		x < b  ->  x-b < 0
		same logic for a > x > b
		(a-x) and (x-b) would have same sign
		 */
		if ((ray.getX1() - x) * (x - ray.getX2()) < 0	//if x in range for ray
				|| (line.getX1() - x) * (x - line.getX2()) < 0	//if x in range for line
				|| (ray.getY1() - y) * (y - ray.getY2()) < 0	//if y in range for ray
				|| (line.getY1() - y) * (y - line.getY2()) < 0)	//if y in range for line
			return null;

		return new Point(x, y);
	}

	private class Point {
		private final double x, y;
		private Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		public double getY() { return y; }
		public double getX() { return x; }
		public double distance(double x, double y) {
			return Point2D.distance(getX(), getY(), x, y);
		}
	}
}
