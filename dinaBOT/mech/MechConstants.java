package dinaBOT.mech;

/**
 * This interface is a collection of static final constants which reflect the physical dimensions of the robot and the arena.
 * <p>
 * All measures must be in cm and in radians
 *
*/
public interface MechConstants {

	//The radius of the robot wheels
	static final double WHEEL_RADIUS = 2.659;

	//The distance from on wheel to the other
//	static final double WHEEL_BASE = 12.7435;
	static final double WHEEL_BASE = 13.1;

	//The distance from one light sensor to another 
	//(currently does not reflect the real distance on purpose, do not change unless you know what you are doing)
	static final double LIGHT_SENSOR_BASE = 16;
	static final double LIGHT_SENSOR_OFFSET = 1;
	
	//The distance from a block to the center of the robot (in cm)
	static final double BLOCK_DISTANCE = 12; //This is an approximation since the robot might not have the block exactly at this distance each time.
	
	//The size of one tile on the course
	static double UNIT_TILE = 30.48;

}