package dinaBOT.navigation;

import dinaBOT.sensor.LineDetectorListener; //The robot will need to be notified of line cross events
import dinaBOT.mech.MechConstants; //The odometer will need to know the robot dimensions

/**
 * The odometer interface specifies the methods which any odometry implementation should conform to.
 * <p>
 * All external packages using odometry should use this interface rather than directly using implementing subclasses.
 * <p>
 * All standard odometers should implement a standard mathematical coordinate systems which follow the right hand rule and have increasing angles in the counterclockwise direction. Angles readings should be continous (eg not bounded to [0,2pi]) in radians. Distances should be measured in cm.
 *
 * @author Severin Smith
 * @see ArcOdometer
 * @see Navigation
 * @see Movement
 * @version 3
*/
public interface Odometer extends Runnable, LineDetectorListener, MechConstants {

	/**
	 * Returns the current position as a double array with a length of three. It will be filled with the x, y and theta components in that order respectively.
	 * <p>
	 * Angles are in radians, x-y coordinates in cm.
	 *
	 * @return position the array with the current position values
	 * @see #setPosition(double[] position, boolean[] update)
	*/
	public double[] getPosition();

	/**
	 * Updates the components of the odometer's position using the values in the <code>position</code> array if and only if the corresponding entry in the masking array <code>update</code> is true.
	 * <p>
	 * Angles are in radians, x-y coordinates in cm.
	 *
	 * @param position the array of update position values
	 * @param update the masking array for updating position values
	 * @see #getPosition()
	*/
	public void setPosition(double[] position, boolean[] update);

	/**
	 * Enable or disable grid snapping (auto correction of the odometer with grid lines).
	 *
	 * @param enable enables grid snapping if set to true, disables it otherwise
	*/
	public void enableSnapping(boolean enable);

	/**
	 * Enable or disable lateral grid snapping (auto correction of the odometer with grid lines not in the direction of travel).
	 *
	 * @param enable enables lateral grid snapping if set to true, disables it otherwise
	*/
	public void enableLateralSnapping(boolean enable);

	/**
	 * Returns the state of the snapping system.
	 *
	 * @return true if currently snapping false otherwise
	*/
	public boolean isSnapping();

	/**
	 * Set the state onscreen debug functionality of the odometer. This feature print the current odometer position to the screen of the NXT brick
	 *
	 * @param state activates the debuging if and only if state is true, deactivates it otherwise
	*/
	public void setDebug(boolean state);

}