package dinaBOT.navigation;

import dinaBOT.sensor.LineDetectorListener;

/**
 * The odometer interface specifies the methods which any odometry implementation should conform to.
 * <p>
 * All external packages using odometry should use this interface rather than directly using implementing subclasses.
 * <p>
 * All standard odometer should implement standard mathematical coordinate systems which follow the right hand rule and have increasing angles in the counterclockwise direction.
 *
 * @author Severin Smith
 * @version 1
*/
public interface Odometer extends Runnable, LineDetectorListener {
	
	/**
	 * Puts the current position into the double array <code>position</code>. The array must have a length of three. It will be filled with the x, y and theta components in that order respectively.
	 *
	 * @return position the array with the current position values
	 * @see #setPosition(double[] position, boolean[] update)
	*/
	public double[] getPosition();	
	
	/**
	 * Updates the components of the odometer's position using the values in the <code>position</code> array if and only if the corresponding entry in the masking array <code>update</code> is true.
	 *
	 * @param position the array of update position values
	 * @param update the masking array for updating position values
	 * @see #getPosition(double[] position)
	*/
	public void setPosition(double[] position, boolean[] update);
	
	/**
	 * Set the state onscreen debug functionaility of the odometer. This feature print the current odometer position to the screen of the NXT brick
	 *
	 * @param state activates the debuging if and only if state is true, deactivates it otherwise
	*/
	public void setDebug(boolean state);
	
	public void localize();
	
	public void setCoor(boolean set);
	
}