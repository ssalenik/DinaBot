package dinaBOT.mech;

/**
 * This interface is a collection of static final constants which reflect the physical dimensions of the robot and the arena.
 * <p>
 * All measures must be in cm and in radians
 *
*/
public interface MechConstants {

	/**The radius of the robot wheels
	 */
	static final double WHEEL_RADIUS = 2.659;

	/**The distance from on wheel to the other = {@value}
	 */	
	static final double WHEEL_BASE = 13;

	/**The distance from one light sensor to another = {@value}
	 *(currently does not reflect the real distance on purpose, do not change unless you know what you are doing)
	 */
	static final double LIGHT_SENSOR_BASE = 16;
	static final double LIGHT_SENSOR_OFFSET = 1;

	/**The distance from the sensor to the center of the cage (in cm) = {@value}
	 */
	static final double BLOCK_DISTANCE = 9;

	/**The size of one tile on the course = {@value}
	 */
	static double UNIT_TILE = 30.48;

	//SPEEDS
	static final int SPEED_ROTATE = 60;
	static final int SPEED_SLOW = 75;
	static final int SPEED_MED = 150;
	static final int SPEED_FAST = 225;

	/**Maximum distance at which a block may be located in order to be detected = {@value}
	 */
	static final int US_TRUST_THRESHOLD = 50;
	
	/**
	* Minimum difference allowed between high and low sensor values to assume both are seeing the same object = {@value}
	*/
	public final int DETECTION_THRESHOLD = 7;
	
	/**
	* Size in degrees of the arc the robot should sweep = {@value}
	*/
	public final double SWEEP_ARC = Math.PI/2;

}
