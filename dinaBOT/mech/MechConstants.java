package dinaBOT.mech;

/**
 * This interface is a collection of static final constants which reflect the physical dimensions of the robot and the arena.
 * <p>
 * All measures must be in cm and in radians
 *
*/
public interface MechConstants {
	
	/**The radius of the robot wheels = {@value}.
	*/
	static final double WHEEL_RADIUS = 2.659;

	/**The distance from on wheel to the other = {@value}.
	*/
	static final double WHEEL_BASE = 12.8;

	/**The distance from one light sensor to another = {@value}
	 *(currently does not reflect the real distance on purpose, do not change unless you know what you are doing)
	*/
	static final double LIGHT_SENSOR_BASE = 23;
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
	static final int SPEED_MED = 200;
	static final int SPEED_FAST = 300;

	/**Maximum distance at which a block may be located in order to be detected = {@value}
	*/
	static final int US_TRUST_THRESHOLD = 30;

	/**
	* Minimum difference allowed between high and low sensor values to assume both are seeing the same object = {@value}
	*/
	public final int DETECTION_THRESHOLD = 25;

	/**
	* Size in degrees of the arc the robot should sweep = {@value}
	*/
	public final double SWEEP_ARC = Math.PI/3;

	/**Maximum distance at which the map will map an obstacle = {@value}.
	*/
	static final int OBSTACLE_THRESHOLD = 45;

	/**Value assigned to nodes containing obstacles in the map and A* pathing algorithm = {@value}.
	*/
	static final int OBSTACLE = 3;	// value which node is considered unpassable

	/**value assigned to nodes in danger zones in the map and A* pathing algorithm = {@value}.
	*/
	static final int DANGER = 2;	// value at which node is considered a danger zoen

	/**Cost of going through a node in a danger zone in the A* pathing algorithm = {@value}.
	*/
	static final int DANGER_COST = 10;	//cost of going through danger zone

	/**Cost of turning (rather than going straight) in the A* pathing algorithm = {@value}.
	*/
	static final int TURN_COST = 1;	//cost of turning

	/**Value assigned to nodes cointaining walls in the map = {@value}.
	*/
	static final int WALL = 10;

	/**Value assigned to nodes which are corners of the drop-off zone in the map = {@value}.
	*/
	static final int DROP_ZONE = 5;

	/**Absolute angle on the map which corresponds to "north" = {@value}; used in pathing related code do determine direction).
	*/
	static final int NORTH = 90;
	/**Absolute angle on the map which corresponds to "south" = {@value}; used in pathing related code do determine direction).
	*/
	static final int SOUTH = 270;
	/**Absolute angle on the map which corresponds to "east" = {@value}; used in pathing related code do determine direction).
	*/
	static final int EAST = 0;
	/**Absolute angle on the map which corresponds to "west" = {@value}; used in pathing related code do determine direction).
	*/
	static final int WEST = 180;

}
