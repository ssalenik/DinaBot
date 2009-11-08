package dinaBOT.navigation;

/**
 * This interface specifies what basic movement capabilites should be implemented. And their basic description.
 *
 * @author Severin Smith
 * @see BasicMovement
 * @see Odometer
 * @see Movement
 * @version 3
*/
public interface Movement {
	
	/**
	 * Makes the robot move forwards a given distance at a given speed in one of the cardinal directions while using the odometer to drive as straight as possible.
	 * <p>
	 * Possible directions: 0 = positive x, 1 = positive y, 2 = negative x, 3 = negative y 
	 *
	 * @param direction the direction to go in (interpreted modulo 4)
	 * @param distance the distance to travel (in cm)
	 * @param speed the speed to travel at
	*/
	public void driveStraight(int direction, double distance, int speed);
	
	/**
	 * Move forward a given distance at a given speed.
	 *
	 * @param distance the distance to travel (in cm)
	 * @param speed the speed to travel at
	*/
	public void goForward(double distance, int speed);
	
	/**
	 * Move forward a given distance at a given speed, return immediately if requested.
	 *
	 * @param distance the distance to travel (in cm)
	 * @param speed the speed to travel at
	 * @param immediateReturn returns immediately if true, blocks otherwise
	*/
	public void goForward(double distance, int speed, boolean immediateReturn);
	
	/**
	 * Rotate a given amount (relative change) at a given speed.
	 *
	 * @param angle the amount to rotate by (in radians)
	 * @param speed the speed to rotate at
	*/
	public void turn(double angle, int speed);

	/**
	 * Rotate a given amount (relative change) at a given speed, return immediately if requested.
	 *
	 * @param angle the amount to rotate by (in radians)
	 * @param speed the speed to rotate at
	 * @param immediateReturn returns immediately if true, blocks otherwise
	*/
	public void turn(double angle, int speed, boolean immediateReturn);
	
	/**
	 * Rotate to a given heading (absolute change) at a given speed.
	 *
	 * @param angle the heading to rotate to (in radians)
	 * @param speed the speed to rotate at
	*/
	public void turnTo(double angle, int speed);

 	/**
	 * Rotate to a given heading (absolute change) at a given speed, return immediately if requested.
 	 *
	 * @param angle the heading to rotate to (in radians)
	 * @param speed the speed to rotate at
	 * @param immediateReturn returns immediately if true, blocks otherwise
	*/
	public void turnTo(double angle, int speed, boolean immediateReturn);
	
	/**
	 * Move forward at a given speed. This is a non blocking method.
	 *
	 * @param speed the speed to move at
	*/
	public void forward(int speed);

	/**
	 * Move backwards at a given speed. This is a non blocking method.
	 *
	 * @param speed the speed to move at
	*/
	public void backward(int speed);

	/**
	 * Rotate clockwise (0) or counterclockwise (1) at a given speed. This is a non blocking method.
	 *
	 * @param direction the direction to rotate in. 0 is clockwise, 1 is counterclockwise.
	 * @param speed the speed to rotate at
	*/
	public void rotate(boolean direction, int speed);

	/**
	 * Stop any ongoing movement.
	 *
	*/
	public void stop();
	
	/**
	 * Return if the robot is movement because of this movement controller
	 *
	 * @return true if the robot is moving false otherwise
	*/
	public boolean isMoving();

}