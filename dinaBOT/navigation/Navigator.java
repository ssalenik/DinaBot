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
	int suspend_count;
	double[][] path;

	double[][] history;
	int history_pointer;

	boolean active, suspend_interrupt, soft_interrupt, full_mode, backtrack;

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

		suspend_count = 2;

		this.map = map;
		this.pathing = pathing;

		low_Readings = new int[] {255,255,255,255,255,255,255,255};
		high_Readings = new int[] {255,255,255,255,255,255,255,255};

		history = new double[2][2]; // 2 nodes + x/y

		map.registerListener(this);

		USSensor.high_sensor.registerListener(this);
		USSensor.low_sensor.registerListener(this);
	}

	/**
	 * Finds a path and goes to the given coordinate (the nearest map node to that coordinate).
	 *
	 * @param x x of the coordinate (in cm).
	 * @param y y of the coordinate (in cm).
	 * @param full if true, dissables interrupts for pallets.
	 * @param pickup_sucess should be false if the last pickup attempt failed.
	 *
	 * @return 0 if succesfully reached the destination coordinate, 1 if interrupted for pallet, -1 if path is impossible.
	*/
	public int goTo(double x, double y, boolean full, boolean pickup_sucess) {
		this.full_mode = full;
		
		if(backtrack) {
			if(map.coordValue(new double[] {x,y}) >= DANGER) return -1;
		}

		if(suspend_interrupt && !pickup_sucess) {
			suspend_count = 0;
		}

		suspend_interrupt = false;

		while(repath(x, y)) {

			soft_interrupt = false;
			for(node = 0; node < path.length; node++) {
				active = true;
				if(suspend_interrupt || soft_interrupt || !movement.goTo(path[node][0], path[node][1], SPEED_MED)) break;
				active = false;

				history[history_pointer%history.length] = path[node];
				history_pointer++;

				if(node == path.length-1) {
					path = null;
					return 0;
				}
				if(suspend_count < 2) suspend_count++;
			}
			active = false;
			if(suspend_interrupt) {
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
		suspend_interrupt = true;
		movement.stop();
	}
	
	public void setBacktrack(boolean set) {
		this.backtrack = set;
	}

	public void backtrack() {
		if(history_pointer > 1) {
			for(int i = 0;i < history.length;i++) {
				movement.goTo(history[(history_pointer-i)%history.length][0], path[(history_pointer-i)%history.length][1], SPEED_MED);
			}
		}
	}

	/**
	 * Interrupts the goTo method if a newly mapped obstacle lies on the current path.
	*/
	public void newObstacle(int x, int y) {
		if(active) {

			for(int i = node ; i < path.length; i++) {
				// check if new obstacle lies on current path
				if((path[i][0] == x*UNIT_TILE) && (path[i][1] == y*UNIT_TILE)) {
					soft_interrupt = true;
					movement.stop();
				}
			}
		}
	}

	/**
	 * Interrupts path if a pallet withing the DETECTION_THRESHOLD is seen.
	*/
	public void newValues(int[] new_values, USSensor sensor) {

		if(active && !full_mode && suspend_count >= 2) {
			int minLow, minHigh;

			if(sensor == USSensor.low_sensor) low_Readings = new_values;
			else if(sensor == USSensor.high_sensor) high_Readings = new_values;
			else return; //should never happen

			minLow = low_Readings[0];
			minHigh = high_Readings[0];

			if(minLow < 20
						&& Math.abs(minLow - minHigh) > DETECTION_THRESHOLD
						/*&& low_Readings[1] < 75*/ && map.checkUSCoord(low_Readings[0])) {
				interrupt();
			}
		}
	}
}
