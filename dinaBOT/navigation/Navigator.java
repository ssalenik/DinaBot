package dinaBOT.navigation;

import dinaBOT.mech.*;
import dinaBOT.sensor.*;

import lejos.nxt.*;

/**
 * Navigator implements the navigation interface. It is the integration point of the Movement, Map and Pathing.
 *
 * @author Stepan Salenikovich, Severin Smith
 * @see Navigation
 * @see Map
 * @see Pathing
 * @see Astar
 * @see MapListener
 * @see USSensorListener
 * @see USSensor
 * @version 1
 */
public class Navigator implements Navigation, MechConstants, USSensorListener {

	/* -- Instance Variables -- */
	
	Odometer odometer;
	Movement movement;

	Map map;
	Pathing pathing;

	int[] low_Readings;
	int[] high_Readings;

	int node; //the current location in the path array
	double[][] path;

	boolean active, hard_interrupt, soft_interrupt, full_mode;

	/**
	 * Instantiate a new Navigator
	 *
	 * @param odometer the odometer object to use
	 * @param movement the movement object to use
	 * @param map the map object to use
	 * @param pathing the pathing object to use
	*/
	public Navigator(Odometer odometer, Movement movement, Map map, Pathing pathing) {
		this.odometer = odometer;
		this.movement = movement;

		this.map = map;
		this.pathing = pathing;

		map.registerListener(this);

		low_Readings = new int[] {255,255,255,255,255,255,255,255};
		high_Readings = new int[] {255,255,255,255,255,255,255,255};

		USSensor.high_sensor.registerListener(this);
		USSensor.low_sensor.registerListener(this);
	}

	public int goTo(double x, double y, boolean full) {
		this.full_mode = full;
		hard_interrupt = false;

		while(repath(x, y)) {
			soft_interrupt = false;
			for(node = 0; node < path.length; node++) {
				active = true;
				if(hard_interrupt || soft_interrupt || !movement.goTo(path[node][0], path[node][1], SPEED_MED)) break;
				active = false;
				if(node == path.length-1) {
					path = null;
					return 0;
				}
			}
			active = false;
			if(hard_interrupt) {
				path = null;
				return 1;
			}
		}
		path = null;
		return -1;
	}

	/**
	 * Regenerate a path to the x,y point from the current position. Return false if possible
	 *
	 * @param x the x position to go to
	 * @param y the position to go to
	 * @return true if the repathing was a success false otherwise
	*/
	boolean repath(double x, double y) {
		double[] position = new double[3];
		position = odometer.getPosition();

		if(path != null && node != 0) {
			position[0] = path[node - 1][0];
			position[1] = path[node - 1][1];
			position[2] = position[2];
		}

		path = pathing.generatePath(position[0], position[1], position[2], x, y);
		if(path == null) return false;
		else return true;
	}

	/**
	 * Interrupt the goTo if it is in progress
	 *
	*/
	public void interrupt() {
		hard_interrupt = true;
		movement.stop();
	}

	public void newObstacle(int x, int y) {
		if(active) {
			System.out.println(x+"--"+y);
			for(int i = node ; i < path.length; i++) {
				// check if new obstacle lies on current path
				if((path[i][0] == x*UNIT_TILE) && (path[i][1] == y*UNIT_TILE)) {
					soft_interrupt = true;
					movement.stop();
				}
			}
		}
	}

	public void newValues(int[] new_values, USSensor sensor) {
		double[] position = odometer.getPosition();

		if(active && !full_mode) {
			int minLow, minHigh;

			if(sensor == USSensor.low_sensor) low_Readings = new_values;
			else if (sensor == USSensor.high_sensor) high_Readings = new_values;
			else return; //should never happen

			minLow = low_Readings[0];
			minHigh = high_Readings[0];

			if(minLow < 20
						&& Math.abs(minLow - minHigh) > DETECTION_THRESHOLD
						&& low_Readings[1] < 75 && map.checkUSCoord((double)low_Readings[0], position[2])) {
				interrupt();

			}
		}
	}
}
